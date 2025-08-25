package com.lgy.controller;

import com.lgy.common.BaseResponse;
import com.lgy.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {


    @GetMapping("/world")
    public BaseResponse<String> helloWorld() {
        return ResultUtils.success("hello world");
    }
}
