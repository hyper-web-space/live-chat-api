package chung.me.livechatapi.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.messaging.MessagingException
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component

@Component
class RedisSubscriber(
  private val simpleMessageSendingOperations: SimpMessageSendingOperations,
  private val objectMapper: ObjectMapper,
) : MessageListenerAdapter() {

  companion object {
    private val logger = LoggerFactory.getLogger(RedisSubscriber::class.java)
  }

  // RedisChatMessage 를 redis sub으로 받아서 웹소켓 /chat/${RedisChatMessage.roomId} 로 메시지를 보낸다.
  override fun onMessage(message: Message, pattern: ByteArray?) {
    val redisChatMessage = objectMapper.readValue<RedisChatMessage>(message.body)
    try {
      simpleMessageSendingOperations.convertAndSend("/chat/${redisChatMessage.roomId}", redisChatMessage)
    } catch (e: MessagingException) {
      logger.error("Failed send message to /chat/${redisChatMessage.roomId}, MessagingException occurred. message: ${e.message}")
    }
  }
}
