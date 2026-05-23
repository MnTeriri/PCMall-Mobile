package com.example.pcmallcompose.core.model.ai

data class AiChatRequest(
    val userId: String,//用户ID
    val sessionId: String,//会话ID
    val message: String,//用户发送的消息内容
    val topK: Int = 5,//候选商品返回数量
)