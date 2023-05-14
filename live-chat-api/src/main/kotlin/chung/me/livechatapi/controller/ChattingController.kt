package chung.me.livechatapi.controller

import chung.me.livechatapi.service.ChatRoomService
import chung.me.livechatapi.service.ChattingService
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Controller

@Controller
class ChattingController(
  private val simpleMessageSendingOperations: SimpMessageSendingOperations,
  private val chatRoomService: ChatRoomService,
  private val chattingService: ChattingService,
) {

  companion object {
    private fun logger() = LoggerFactory.getLogger(ChattingController::class.java)
  }

  @MessageMapping("/message/{roomId}")
  fun receiveMessage(
    @DestinationVariable roomId: String,
    chatData: ChatData,
  ) {

    if (chatRoomService.isRoomClosed(roomId)) {
      logger().debug("chat room is closed. [$roomId]")
      return
    }

    chattingService.saveAndPublish(roomId, chatData)

    logger().debug("send message to /chat/$roomId")
    simpleMessageSendingOperations.convertAndSend("/chat/$roomId", chatData)
  }
}
