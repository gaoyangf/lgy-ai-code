package com.lgy.ai;

import com.lgy.ai.model.HtmlCodeResult;
import com.lgy.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeGeneratorServiceTest {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;
    @Test
    void generateCode() {
        HtmlCodeResult s = aiCodeGeneratorService.generateCode("做个篮球网站的博客，不超过20行");
        Assertions.assertNotNull(s);
    }

    @Test
    void generateMultiFileCode() {
        MultiFileCodeResult s = aiCodeGeneratorService.generateMultiFileCode("做个篮球网站的博客，不超过50行");
        Assertions.assertNotNull(s);
    }
}