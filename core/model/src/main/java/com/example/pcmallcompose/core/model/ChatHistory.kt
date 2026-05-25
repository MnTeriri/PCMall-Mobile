package com.example.pcmallcompose.core.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.time.LocalDateTime

data class ChatHistory(
    val type: ChatHistoryType,      //聊天信息类型（AI、USER）
    val content: String,            //聊天信息
    val recommends: List<Goods>,    //推荐信息
    val chatStatus: ChatStatus,     //聊天状态（CHATTING、FINISH）
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

    enum class ChatStatus(
        @field:JsonValue
        val code: Int,
        val label: String
    ) {
        CHATTING(0, "CHATTING"),
        FINISH(1, "FINISH"),
        ERROR(2, "ERROR");

        @JsonCreator
        fun fromCode(code: Int): ChatStatus? {
            for (state in entries) {
                if (state.code == code) {
                    return state
                }
            }
            return null
        }
    }
}