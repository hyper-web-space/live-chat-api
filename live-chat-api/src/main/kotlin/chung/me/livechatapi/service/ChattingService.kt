package chung.me.livechatapi.service

import chung.me.livechatapi.redis.RedisPublisher
import chung.me.livechatmessage.dto.ChatData
import org.springframework.stereotype.Service

@Service
class ChattingService(
  private val redisPublisher: RedisPublisher,
) {

  fun publishMessageToRedis(roomId: String, chatData: ChatData) {
    redisPublisher.publish(roomId, chatData)
  }
}
