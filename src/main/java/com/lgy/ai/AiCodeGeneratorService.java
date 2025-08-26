package com.lgy.ai;

import com.lgy.ai.model.HtmlCodeResult;
import com.lgy.ai.model.MultiFileCodeResult;
import dev.langchain4j.service.SystemMessage;
import reactor.core.publisher.Flux;


public interface AiCodeGeneratorService {


    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    HtmlCodeResult generateCode(String userMessage);


    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    MultiFileCodeResult generateMultiFileCode(String userMessage);
    /**
     * 生成 HTML 代码
     *
     * @param userMessage 用户提示词
     * @return AI 的输出结果
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    Flux<String> generateHtmlCodeStream(String userMessage);

    /**
     * 生成多文件代码
     *
     * @param userMessage 用户提示词
     * @return AI 的输出结果
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    Flux<String> generateMultiFileCodeStream(String userMessage);
//
//    /**
//     * 生成 Vue 项目代码（流式）
//     *
//     * @param userMessage 用户提示词
//     * @return AI 的输出结果
//     */
//    @SystemMessage(fromResource = "prompt/codegen-vue-project-system-prompt.txt")
//    TokenStream generateVueProjectCodeStream(@MemoryId long appId, @UserMessage String userMessage);
}
