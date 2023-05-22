package chung.me.livechatsaver.redis

import chung.me.livechatmessage.dto.ChatData
import chung.me.livechatmessage.redis.RedisChatMessage
import chung.me.livechatsaver.service.ChattingService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.stereotype.Component

@Component
class RedisSubscriber(
  private val objectMapper: ObjectMapper,
  private val chattingService: ChattingService,
) : MessageListenerAdapter() {

  override fun onMessage(message: Message, pattern: ByteArray?) {
    val redisMessage = objectMapper.readValue<RedisChatMessage>(message.body)
    chattingService.saveMessage(redisMessage.roomId, ChatData.of(redisMessage))
  }
}
