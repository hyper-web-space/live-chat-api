package chung.me.livechatsaver.config

import chung.me.livechatsaver.redis.RedisSubscriber
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer

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
}
