package com.example.pcmallcompose.core.network.sse

import android.util.Log
import com.example.pcmallcompose.core.model.ai.AiChatEvent
import com.example.pcmallcompose.core.model.ai.AiChatRequest
import com.example.pcmallcompose.core.network.di.NetworkModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener

@Singleton
class AiChatSseClient @Inject constructor(
    private val eventSourceFactory: EventSource.Factory
) {
    companion object {
        private const val TAG = "AiChatSseClient"
        private const val CHAT_PATH = "ai/assistant/chat"
        private val JSON_MEDIA_TYPE = "application/json".toMediaType()
    }

    private val objectMapper = jacksonObjectMapper()

    fun chat(
        uid: String,
        sessionId: String,
        message: String
    ): Flow<AiChatEvent> = callbackFlow {
        val jsonBody = objectMapper.writeValueAsString(AiChatRequest(uid, sessionId, message))
            .toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url("${NetworkModule.BASE_URL}$CHAT_PATH")
            .post(jsonBody)
            .build()

        val eventSource = eventSourceFactory.newEventSource(request, object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                Log.d(TAG, "SSE 已连接: ${response.code}")
            }

            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                Log.d(TAG, "SSE 收到消息： type=$type, data=$data")
                val event = objectMapper.readValue(data, AiChatEvent::class.java)
                trySend(event)
            }

            // 出错时传递异常
            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                Log.e(TAG, "SSE 连接失败", t)
                close(t)
            }

            // 流结束时关闭 Flow
            override fun onClosed(eventSource: EventSource) {
                Log.d(TAG, "SSE 连接关闭")
                close()
            }
        })

        awaitClose { eventSource.cancel() }
    }
}