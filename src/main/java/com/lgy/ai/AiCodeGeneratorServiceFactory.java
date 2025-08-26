package com.lgy.ai;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.lgy.service.impl.ChatHistoryServiceImpl;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import java.time.Duration;

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

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Resource
    private ChatHistoryServiceImpl chatHistoryService;

    /**
     * AI 服务实例缓存
     * 缓存策略：
     * - 最大缓存 1000 个实例
     * - 写入后 30 分钟过期
     * - 访问后 10 分钟过期
     */
    private final Cache<Long, AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 服务实例被移除，缓存键: {}, 原因: {}", key, cause);
            })
            .build();


    /**
     *  根据appId获取AI服务实例
     *
     * */
    public AiCodeGeneratorService getAiCodeGeneratorService(long appId) {
        return serviceCache.get(appId, k -> createAiCodeGeneratorService(appId));
    }


    /**
     * 创建新的 AI 服务实例
     *
     * @param appId       应用 id
     * @return
     */
    public AiCodeGeneratorService createAiCodeGeneratorService(long appId) {
      log.info("为appId:{},创建新的ai服务实例", appId);
       MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
               .id(appId)
               .chatMemoryStore(redisChatMemoryStore)
               .maxMessages(20)
               .build();
        // 从数据库中加载对话历史到记忆中
        chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 20);
       return AiServices.builder(AiCodeGeneratorService.class)
               .chatModel(chatModel)
               .streamingChatModel(streamingChatModel)
               .chatMemory(chatMemory)
               .build();

    }
}
