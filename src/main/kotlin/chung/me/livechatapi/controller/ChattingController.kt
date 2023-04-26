package chung.me.livechatapi.controller

import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Controller

@Controller
class ChattingController(
  private val simpleMessageSendingOperations: SimpMessageSendingOperations
) {

  @MessageMapping("/message/{roomId}")
  fun receiveMessage(
    @DestinationVariable roomId: String,
    chatData: ChatData,
  ) {
    simpleMessageSendingOperations.convertAndSend("/chat/$roomId", chatData)
  }
}
