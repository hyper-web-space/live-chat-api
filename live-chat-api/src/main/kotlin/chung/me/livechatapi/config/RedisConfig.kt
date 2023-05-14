package chung.me.livechatapi.config

import chung.me.livechatapi.redis.RedisSubscriber
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {
  @Bean
  fun redisMessageListenerContainer(
    connectionFactory: RedisConnectionFactory,
    redisSubscriber: RedisSubscriber
  ): RedisMessageListenerContainer {
    return RedisMessageListenerContainer()
      .also {
        it.setConnectionFactory(connectionFactory)
        it.addMessageListener(redisSubscriber, ChannelTopic("chat"))
      }
  }

  @Bean
  fun <T : Any> redisTemplate(
    connectionFactory: RedisConnectionFactory,
    objectMapper: ObjectMapper,
  ): RedisTemplate<String, T> {
    return RedisTemplate<String, T>().also {
      it.setConnectionFactory(connectionFactory)
      it.keySerializer = StringRedisSerializer()
      it.valueSerializer = Jackson2JsonRedisSerializer(objectMapper, String::class.java)
    }
  }
}
