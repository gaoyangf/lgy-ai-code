package com.lgy.core;

import com.lgy.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class AiCodeGeneratorFacadeTest {
    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;
    @Test
    void generateAndSaveCode() {
        File loginFile = aiCodeGeneratorFacade.generateAndSaveCode("生成一个登录界面,不超过20行代码", CodeGenTypeEnum.HTML);
        Assertions.assertNotNull(loginFile);
    }



    @Test
    void generateStreamAndSaveCode() {
        Flux<String> stream = aiCodeGeneratorFacade.generateStreamAndSaveCode("生成一个登录界面", CodeGenTypeEnum.MULTI_FILE);
        List<String> block = stream.collectList().block();
        Assertions.assertNotNull(block);
    }
}