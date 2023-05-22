package chung.me.livechatmessage.dto

import chung.me.livechatmessage.entity.Message
import chung.me.livechatmessage.redis.RedisChatMessage
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class ChatData(
  val sender: String,
  val contents: String,
  @get:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  val messageTimestamp: LocalDateTime,
) {
  companion object {
    fun of(message: Message): ChatData {
      return ChatData(message.sender, message.content, message.createdAt)
    }

    fun of(redisMessage: RedisChatMessage): ChatData {
      return ChatData(redisMessage.sender, redisMessage.content, redisMessage.messageTimestamp)
    }
  }
}
