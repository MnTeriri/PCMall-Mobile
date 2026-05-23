package com.example.pcmallcompose.core.model.ai

data class AiChatEvent(
    val type: AiChatEventType,
    val data: Any,
) {

    enum class AiChatEventType {
        START,  // 开始处理请求
        INTENT,  // 解析用户购买意图
        GOODS,  // 返回候选商品列表
        RAG,  // 返回RAG知识检索结果
        TEXT,  // 流式文本内容（模型生成过程）
        DONE,  // 处理完成
        ERROR,  // 错误信息
    }
}