package chung.me.livechatmessage.dto

import chung.me.livechatmessage.entity.Message
import java.time.LocalDateTime

data class ChatData(
  val sender: String,
  val contents: String,
  val messageTimestamp: LocalDateTime,
) {
  companion object {
    fun fromMessage(message: Message): ChatData {
      return ChatData(message.sender, message.content, message.createdAt)
    }
  }
}
