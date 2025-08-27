package com.lgy.core;

import com.lgy.ai.AiCodeGeneratorService;
import com.lgy.ai.AiCodeGeneratorServiceFactory;
import com.lgy.ai.model.HtmlCodeResult;
import com.lgy.ai.model.MultiFileCodeResult;
import com.lgy.core.parser.CodeParserExecutor;
import com.lgy.core.saver.CodeFileSaverExecutor;
import com.lgy.exception.BusinessException;
import com.lgy.exception.ErrorCode;
import com.lgy.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * AI 代码生成门面类，组合代码生成和保存功能
 */
@Service
@Slf4j
public class AiCodeGeneratorFacade {


    @Resource
    private AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;
    /**
     * 统一入口：根据类型生成并保存代码
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @return 保存的目录
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum,Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成类型不能为空");
        }
        // 根据 appId 获取相应的 AI 服务实例
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        // 根据 appId 获取相应的 AI 服务实例
        return switch (codeGenTypeEnum) {
            case HTML ->  {
                HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateCode(userMessage);
                yield  CodeFileSaverExecutor.executeSaver(htmlCodeResult, codeGenTypeEnum,appId);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
                yield  CodeFileSaverExecutor.executeSaver(multiFileCodeResult, codeGenTypeEnum,appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }

    /**
     * 统一入口：根据类型生成并保存代码 (流式)
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @return 保存的目录
     */
    public Flux<String> generateStreamAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum,Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成类型不能为空");
        }
        // 根据 appId 获取相应的 AI 服务实例
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId, codeGenTypeEnum);
        return switch (codeGenTypeEnum) {
            case VUE_PROJECT -> {
                Flux<String> codeStream = aiCodeGeneratorService.generateVueProjectCodeStream(appId, userMessage);
                yield  processFileCodeStream(codeStream, codeGenTypeEnum,appId);
            }
            case HTML -> {
                Flux<String> codeStream = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
                yield  processFileCodeStream(codeStream, codeGenTypeEnum,appId);
            }
            case MULTI_FILE ->{
                Flux<String> codeStream = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
                yield processFileCodeStream(codeStream, codeGenTypeEnum,appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }


    /**
     * 统一解析保存入口
     *
     * @param codeStream 用户提示词
     * @param codeGenTypeEnum 生成类型
     * @return  保存的目录
     * */
    private Flux<String> processFileCodeStream(Flux<String> codeStream,CodeGenTypeEnum codeGenTypeEnum,Long appId) {
        //定义一个字符串拼接器
        StringBuilder stringBuilder = new StringBuilder();
        return codeStream.doOnNext(chunk -> {
            // 实时收集代码片段
            stringBuilder.append(chunk);
        }).doOnComplete(()->{
            try {
                // 流式返回全部完成后，保存代码
                String completeCode = stringBuilder.toString();
                // 解析代码
                Object parsedResult = CodeParserExecutor.executeParser(completeCode, codeGenTypeEnum);
                // 保持云文件
                File  file =  CodeFileSaverExecutor.executeSaver(parsedResult, codeGenTypeEnum,appId);
                log.info("保存的目录：{}", file.getAbsolutePath());
            }catch (Exception e){
                log.error("保存代码失败：{}", e.getMessage());
            }

        });
    }

}
