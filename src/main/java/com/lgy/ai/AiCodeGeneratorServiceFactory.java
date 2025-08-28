package com.lgy.ai;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.lgy.ai.guardrail.PromptSafetyInputGuardrail;
import com.lgy.ai.tools.ToolManager;
import com.lgy.exception.BusinessException;
import com.lgy.exception.ErrorCode;
import com.lgy.model.enums.CodeGenTypeEnum;
import com.lgy.service.impl.ChatHistoryServiceImpl;
import com.lgy.utils.SpringContextUtil;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
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


    @Resource(name = "openAiChatModel")
    private ChatModel chatModel;

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Resource
    private ChatHistoryServiceImpl chatHistoryService;

    @Resource
    private ToolManager toolManager;
    /**
     * AI 服务实例缓存
     * 缓存策略：
     * - 最大缓存 1000 个实例
     * - 写入后 30 分钟过期
     * - 访问后 10 分钟过期
     */
    private final Cache<String, AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
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
        return getAiCodeGeneratorService(appId, CodeGenTypeEnum.HTML);
    }
    /**
     * 根据 appId 获取服务
     *
     * @param appId       应用 id
     * @param codeGenType 生成类型
     * @return
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenType) {
        String cacheKey = buildCacheKey(appId, codeGenType);
        return serviceCache.get(cacheKey, key -> createAiCodeGeneratorService(appId, codeGenType));
    }

    /**
     * 创建新的 AI 服务实例
     * @param codeGenTypeEnum  类型
     * @param appId       应用 id
     * @return
     */
    public AiCodeGeneratorService createAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenTypeEnum) {
      log.info("为appId:{},创建新的ai服务实例", appId);
       MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
               .id(appId)
               .chatMemoryStore(redisChatMemoryStore)
               .maxMessages(20)
               .build();
        // 从数据库中加载对话历史到记忆中
        chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 20);
        return switch (codeGenTypeEnum) {
            case HTML, MULTI_FILE -> {
                // 使用多例模式的 StreamingChatModel 解决并发问题
                StreamingChatModel openAiStreamingChatModel = SpringContextUtil.getBean("streamingChatModelPrototype", StreamingChatModel.class);
                yield  AiServices.builder(AiCodeGeneratorService.class)
                        .chatModel(chatModel)
                        .streamingChatModel(openAiStreamingChatModel)
                        .chatMemory(chatMemory)
                        .inputGuardrails(new PromptSafetyInputGuardrail()) // 添加输入护轨
                        .build();
            }
            case VUE_PROJECT -> {
                StreamingChatModel reasoningStreamingChatModel = SpringContextUtil.getBean("reasoningStreamingChatModelPrototype", StreamingChatModel.class);
                yield   AiServices.builder(AiCodeGeneratorService.class)
                        .chatModel(chatModel)
                        .streamingChatModel(reasoningStreamingChatModel)
                        .chatMemoryProvider(memoryId -> chatMemory)
                        .tools(toolManager.getAllTools())
                        .inputGuardrails(new PromptSafetyInputGuardrail()) // 添加输入护轨
                        // 处理工具调用幻觉问题
                        .hallucinatedToolNameStrategy(toolExecutionRequest ->
                                        ToolExecutionResultMessage.from(toolExecutionRequest,
                                                "Error: there is no tool called " + toolExecutionRequest.name())
                                ).maxSequentialToolsInvocations(20)  // 最多连续调用 20 次工具

                        .build();
            }
            default ->
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: " + codeGenTypeEnum.getValue());
        };

    }
    /**
     * 构造缓存键
     *
     * @param appId
     * @param codeGenType
     * @return
     */
    private String buildCacheKey(long appId, CodeGenTypeEnum codeGenType) {
        return appId + "_" + codeGenType.getValue();
    }
}
