package chung.me.livechatapi.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@TestConfiguration
@EnableWebSocketMessageBroker
class WebSocketMessageBrokerConfig(
  private val authChannelInterceptorAdaptor: AuthChannelInterceptorAdaptor,
) : WebSocketMessageBrokerConfigurer {
  override fun configureMessageBroker(registry: MessageBrokerRegistry) {
    // 클라이언트가 구독하는 경로, 이 경로를 구독한 클라이언트로 메시지를 발송한다
    registry.enableSimpleBroker("/chat")
    // 메시지 브로커가 처리할 요청 경로, 클라이언트가 이 경로로 메시지를 보낸다.
    registry.setApplicationDestinationPrefixes("/queue")
  }

  override fun registerStompEndpoints(registry: StompEndpointRegistry) {
    registry.addEndpoint("/ws")
      .setAllowedOriginPatterns("*")
      .withSockJS()
  }

  override fun configureClientInboundChannel(registration: ChannelRegistration) {
    registration.interceptors(authChannelInterceptorAdaptor)
  }
}
