package com.lgy.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 服务创建工厂
 */
@Configuration
@Slf4j
public class AiCodeGeneratorServiceFactory {

    @Resource
    private ChatModel chatModel;

    @Resource
    private StreamingChatModel streamingChatModel;

   @Bean
    public AiCodeGeneratorService createAiCodeGeneratorService() {
       return AiServices.builder(AiCodeGeneratorService.class)
               .chatModel(chatModel)
               .streamingChatModel(streamingChatModel)
               .build();
    }
}
