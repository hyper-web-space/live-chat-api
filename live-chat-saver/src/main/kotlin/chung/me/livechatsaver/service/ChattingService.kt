package chung.me.livechatsaver.service

import chung.me.livechatmessage.dto.ChatData
import chung.me.livechatmessage.entity.Message
import chung.me.livechatsaver.repos.MessageRepos
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChattingService(
  private val messageRepos: MessageRepos,

) {

  @Transactional
  fun saveMessage(roomId: String, chatData: ChatData) {
    println("roomId: $roomId, chatData: $chatData")

    messageRepos.save(
      Message(
        roomId = ObjectId(roomId),
        sender = chatData.sender,
        content = chatData.contents
      ).apply {
        createdAt = chatData.messageTimestamp
      }
    )
  }
}
