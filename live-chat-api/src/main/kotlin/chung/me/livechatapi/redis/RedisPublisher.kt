package chung.me.livechatapi.redis

import chung.me.livechatmessage.dto.ChatData
import chung.me.livechatmessage.redis.RedisChatMessage
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisPublisher(
  private val redisTemplate: RedisTemplate<String, RedisChatMessage>,
) {

  fun publish(roomId: String, chatData: ChatData) {
    RedisChatMessage.of(roomId, chatData).let {
      redisTemplate.convertAndSend("chat", it)
    }
  }
}
