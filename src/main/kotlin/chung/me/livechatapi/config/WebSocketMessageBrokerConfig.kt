package chung.me.livechatapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketMessageBrokerConfig(
  @Value("\${spring.redis.host}") private val redisHost: String,
  @Value("\${spring.redis.port}") private val redisPort: Int,
  @Value("\${spring.redis.username}") private val redisUsername: String,
  @Value("\${spring.redis.password}") private val redisPassword: String,
) : WebSocketMessageBrokerConfigurer {
  override fun configureMessageBroker(registry: MessageBrokerRegistry) {
    // 클라이언트가 구독하는 경로, 이 경로를 구독한 클라이언트로 메시지를 발송한다
    registry.enableStompBrokerRelay("/queue")
      .setRelayHost(redisHost)
      .setRelayPort(redisPort)
      .setClientLogin(redisUsername)
      .setClientPasscode(redisPassword)
    // 메시지 브로커가 처리할 요청 경로, 클라이언트가 이 경로로 메시지를 보낸다.
    registry.setApplicationDestinationPrefixes("/chat")
  }

  override fun registerStompEndpoints(registry: StompEndpointRegistry) {
    registry.addEndpoint("/ws")
      .setAllowedOriginPatterns("*")
  }
}
