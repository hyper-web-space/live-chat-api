package chung.me.livechatapi.controller

import chung.me.livechatapi.config.USER_ID
import chung.me.livechatapi.entity.ChatRoom
import chung.me.livechatapi.service.ChatRoomService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("chatrooms")
class ChatRoomController(
  private val service: ChatRoomService,
) {

  @Operation(

    responses = [
      ApiResponse(
        responseCode = "200",
        description = "생성 성공",
        content = [
          Content(schema = Schema(implementation = CreationChatRoomResponse::class), mediaType = "application/json")
        ]
      ),
      ApiResponse(
        responseCode = "401",
        description = "AUTHORIZATION 헤더가 없거나, 유효하지 않은 토큰",
        content = [
          Content(
            schema = Schema(implementation = ResponseErrorEntity::class),
            mediaType = "application/json"
          )
        ]
      ),
    ],
    description = "채팅방 생성"
  )
  @PostMapping
  fun createChatRoom(
    @RequestHeader(USER_ID) userId: String,
    @RequestBody body: CreationChatRoomBody,
  ): ResponseEntity<CreationChatRoomResponse> {
    val (name, password) = body
    return ResponseEntity.ok(service.createChatRoom(name, password, userId))
  }
}

data class CreationChatRoomBody(
  val name: String,
  val password: String? = null,
)

data class CreationChatRoomResponse(
  val name: String,
  val creator: String,
  val privateRoom: Boolean,
  val createdAt: LocalDateTime,
) {
  companion object {
    fun fromChatRoom(chatRoom: ChatRoom) = CreationChatRoomResponse(
      chatRoom.name,
      chatRoom.creator,
      chatRoom.privateRoom,
      chatRoom.createdAt,
    )
  }
}
