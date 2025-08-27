package com.lgy.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class WebScreenshotUtilsTest {

    @Test
    void saveWebPageScreenshot() {
        String path = "http://localhost:81/93YU2Y/#/";
        String savePath = WebScreenshotUtils.saveWebPageScreenshot(path);
        Assertions.assertNotNull(savePath);
    }
}