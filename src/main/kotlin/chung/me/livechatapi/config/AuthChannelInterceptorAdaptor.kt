package chung.me.livechatapi.config

import chung.me.livechatapi.service.AuthService
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component

@Component
class AuthChannelInterceptorAdaptor(
  private val authService: AuthService,
) : ChannelInterceptor {

  override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
    val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

    if (accessor != null && StompCommand.CONNECT == accessor.command) {
      val authorizationHeader = accessor.getNativeHeader("Authorization") ?: return null

      authorizationHeader[0]?.let { token ->
        accessor.user = authService.getUsernamePasswordAuthenticationToken(token)
      }
    }

    return message
  }
}
