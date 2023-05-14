package chung.me.livechatapi.redis

import chung.me.livechatapi.controller.ChatData
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
