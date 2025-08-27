package com.lgy.core;

import com.lgy.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

@SpringBootTest
class AiCodeGeneratorFacadeTest {
    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;
    @Test
    void generateAndSaveCode() {
        File loginFile = aiCodeGeneratorFacade.generateAndSaveCode("生成一个登录界面,不超过20行代码", CodeGenTypeEnum.HTML,317824972761726976L);
        Assertions.assertNotNull(loginFile);
    }



    @Test
    void generateStreamAndSaveCode() {
        Flux<String> stream = aiCodeGeneratorFacade.generateStreamAndSaveCode("生成一个登录界面", CodeGenTypeEnum.MULTI_FILE,317824972761726976L);
        List<String> block = stream.collectList().block();
        Assertions.assertNotNull(block);
    }
    @Test
    void generateVueProjectCodeStream() {
        Flux<String> codeStream = aiCodeGeneratorFacade.generateStreamAndSaveCode(
                "简单的任务记录网站，总代码量不超过 200 行",
                CodeGenTypeEnum.VUE_PROJECT, 1L);
        // 阻塞等待所有数据收集完成
        List<String> result = codeStream.collectList().block();
        // 验证结果
        Assertions.assertNotNull(result);
        String completeContent = String.join("", result);
        Assertions.assertNotNull(completeContent);
    }
}