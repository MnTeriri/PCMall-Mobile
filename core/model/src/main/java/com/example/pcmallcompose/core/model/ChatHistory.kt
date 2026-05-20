package com.example.pcmallcompose.core.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.time.LocalDateTime

data class ChatHistory(
    val type: ChatHistoryType,
    val content: String,
    val createTime: LocalDateTime
) {

    enum class ChatHistoryType(
        @field:JsonValue
        val code: Int,
        val label: String
    ) {
        AI(0, "AI"),
        USER(1, "USER");

        @JsonCreator
        fun fromCode(code: Int): ChatHistoryType? {
            for (state in entries) {
                if (state.code == code) {
                    return state
                }
            }
            return null
        }
    }
}