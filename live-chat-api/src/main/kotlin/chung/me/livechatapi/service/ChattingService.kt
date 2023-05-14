package chung.me.livechatapi.service

import chung.me.livechatapi.controller.ChatData
import chung.me.livechatapi.entity.Message
import chung.me.livechatapi.redis.RedisPublisher
import chung.me.livechatapi.repos.MessageRepos
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChattingService(
  private val messageRepos: MessageRepos,
  private val redisPublisher: RedisPublisher,
) {

  @Transactional
  fun saveAndPublish(roomId: String, chatData: ChatData) {
    saveMessage(roomId, chatData)
    publishMessageToRedis(roomId, chatData)
  }

  // fixme : 나중에 워커로 이동될 예정
  private fun saveMessage(roomId: String, chatData: ChatData) {
    messageRepos.save(
      Message(
        roomId = ObjectId(roomId),
        sender = chatData.sender,
        content = chatData.contents
      )
    )
  }

  private fun publishMessageToRedis(roomId: String, chatData: ChatData) {
    redisPublisher.publish(roomId, chatData)
  }
}
