package chung.me.livechatmessage.redis

import chung.me.livechatmessage.dto.ChatData
import java.io.Serializable
import java.time.LocalDateTime

data class RedisChatMessage(
  val roomId: String,
  val sender: String,
  val content: String,
  val messageTimestamp: LocalDateTime,
) : Serializable {
  companion object {
    fun of(roomId: String, chatData: ChatData) =
      RedisChatMessage(
        roomId = roomId,
        sender = chatData.sender,
        content = chatData.contents,
        messageTimestamp = chatData.messageTimestamp
      )
  }
}
