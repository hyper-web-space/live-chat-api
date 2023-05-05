package chung.me.livechatapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.io.Serializable

@Configuration
class RedisConfig {
  @Bean
  fun redisMessageListenerContainer(
    connectionFactory: RedisConnectionFactory,
    listenerAdapter: MessageListenerAdapter,
    channelTopic: ChannelTopic,
  ): RedisMessageListenerContainer {
    return RedisMessageListenerContainer()
      .also {
        it.setConnectionFactory(connectionFactory)
        it.addMessageListener(listenerAdapter, channelTopic)
      }
  }

  @Bean
  fun listenerAdapter(): MessageListenerAdapter {
    return MessageListenerAdapter(DefaultMessageDelegate(), "handleMessage")
  }

  @Bean
  fun channelTopic(): ChannelTopic {
    return ChannelTopic("chat")
  }

  @Bean
  fun redisTemplate(
    connectionFactory: RedisConnectionFactory,
  ): RedisTemplate<String, Any> {
    return RedisTemplate<String, Any>().also {
      it.setConnectionFactory(connectionFactory)
      it.keySerializer = StringRedisSerializer()
      it.valueSerializer = Jackson2JsonRedisSerializer(String::class.java)
    }
  }
}

interface MessageDelegate {
  fun handleMessage(message: String?)
  fun handleMessage(message: Map<*, *>?)
  fun handleMessage(message: ByteArray?)
  fun handleMessage(message: Serializable?)

  // pass the channel/pattern as well
  fun handleMessage(message: Serializable?, channel: String?)
}

class DefaultMessageDelegate : MessageDelegate { // implementation elided for clarity...
  override fun handleMessage(message: String?) {
    println(message)
  }

  override fun handleMessage(message: Map<*, *>?) {
    println(message)
  }

  override fun handleMessage(message: ByteArray?) {
    println(message)
  }

  override fun handleMessage(message: Serializable?) {
    println(message)
  }

  override fun handleMessage(message: Serializable?, channel: String?) {
    println(message)
  }
}

data class RedisMessage(
  val roomId: String,
  val message: String,
)
