package chung.me.livechatapi.controller

import chung.me.livechatapi.config.USER_ID
import chung.me.livechatapi.entity.ChatRoom
import chung.me.livechatapi.service.ChatRoomService
import chung.me.livechatmessage.dto.ChatData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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

  @Operation(
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content = [
          Content(schema = Schema(implementation = ChatRoomPageResponse::class), mediaType = "application/json")
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
    description = "채팅방 조회"
  )
  @GetMapping
  fun getChatRooms(
    @RequestParam offset: Int,
    @RequestParam limit: Int,
    @RequestParam(required = false) name: String?,
  ): ResponseEntity<ChatRoomPageResponse> {
    return ResponseEntity.ok(service.getChatRooms(offset, limit, name))
  }

  @Operation(
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content = [
          Content(schema = Schema(implementation = ChatRoomPageResponse::class), mediaType = "application/json")
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
    description = "참가 중인 채팅방 조회"
  )
  @GetMapping("connected")
  fun getConnectedChatRooms(
    @RequestParam offset: Int,
    @RequestParam limit: Int,
    @RequestHeader(USER_ID) userId: String,
  ): ResponseEntity<ChatRoomPageResponse> {
    return ResponseEntity.ok(service.getConnectedChatRooms(offset, limit, userId))
  }

  @Operation(
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "입장 성공",
        content = [
          Content(schema = Schema(implementation = ChatRoomResponse::class), mediaType = "application/json")
        ]
      ),
      ApiResponse(
        responseCode = "400",
        description = "이미 입장했거나, 채팅방이 존재하지 않거나, 비밀번호가 틀림",
        content = [
          Content(
            schema = Schema(implementation = ResponseErrorEntity::class),
            mediaType = "application/json"
          )
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
      ApiResponse(
        responseCode = "403",
        description = "채팅방 주인이 나가서 입장할 수 없음",
        content = [
          Content(
            schema = Schema(implementation = ResponseErrorEntity::class),
            mediaType = "application/json"
          )
        ]
      ),
    ],
    description = "채팅방 입장"
  )
  @PostMapping("{chatRoomId}")
  fun joinChatRoom(
    @RequestHeader(USER_ID) userId: String,
    @PathVariable chatRoomId: String,
    @RequestBody body: JoinChatRoomBody,
  ): ResponseEntity<List<ChatData>> {
    val (password) = body
    return ResponseEntity.ok(service.joinChatRoom(chatRoomId, password, userId))
  }

  @Operation(
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "채팅방 나가기 성공",
      ),
      ApiResponse(
        responseCode = "400",
        description = "채팅방이 존재하지 않음",
        content = [
          Content(
            schema = Schema(implementation = ResponseErrorEntity::class),
            mediaType = "application/json"
          )
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
    description = "채팅방 나가기"
  )
  @PutMapping("{chatRoomId}")
  fun exitChatRoom(
    @RequestHeader(USER_ID) userId: String,
    @PathVariable chatRoomId: String,
  ): ResponseEntity<Boolean> {
    return ResponseEntity.ok(service.exitChatRoom(chatRoomId, userId))
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

data class ChatRoomPageResponse(
  val chatRooms: List<ChatRoomResponse>,
  val total: Long,
)

data class ChatRoomResponse(
  val chatRoomId: String,
  val name: String,
  val creator: String,
  val privateRoom: Boolean,
  val createdAt: LocalDateTime,
  val numberOfUser: Int,
) {
  companion object {
    fun fromChatRoom(chatRoom: ChatRoom) = ChatRoomResponse(
      chatRoom.id.toString(),
      chatRoom.name,
      chatRoom.creator,
      chatRoom.privateRoom,
      chatRoom.createdAt,
      chatRoom.participants.count(),
    )
  }
}

data class JoinChatRoomBody(
  val password: String? = null,
)
