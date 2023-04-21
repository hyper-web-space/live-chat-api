package chung.me.livechatapi.service

import chung.me.livechatapi.controller.ChatData
import chung.me.livechatapi.controller.ChatRoomPageResponse
import chung.me.livechatapi.controller.ChatRoomResponse
import chung.me.livechatapi.controller.CreationChatRoomResponse
import chung.me.livechatapi.entity.ChatRoom
import chung.me.livechatapi.repos.ChatRoomRepos
import chung.me.livechatapi.repos.MessageRepos
import org.bson.types.ObjectId
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatusCode
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import kotlin.jvm.optionals.getOrElse

private const val DEFAULT_CHAT_COUNT = 100

@Service
class ChatRoomService(
  private val repos: ChatRoomRepos,
  private val passwordEncoder: PasswordEncoder,
  private val messageRepos: MessageRepos,
) {
  fun createChatRoom(name: String, password: String?, userId: String): CreationChatRoomResponse {
    val newChatRoom = repos.save(ChatRoom(name, userId, password?.let { passwordEncoder.encode(it) }))
    return CreationChatRoomResponse.fromChatRoom(newChatRoom)
  }

  fun getChatRooms(offset: Int, limit: Int, name: String?): ChatRoomPageResponse {
    val pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending())

    return if (name.isNullOrBlank()) {
      repos.findAll(pageable)
    } else {
      repos.findByNameLikeIgnoreCase(
        name, pageable
      )
    }.let {
      ChatRoomPageResponse(
        it.content.map(ChatRoomResponse::fromChatRoom),
        it.totalElements
      )
    }
  }

  fun getConnectedChatRooms(offset: Int, limit: Int, userId: String): ChatRoomPageResponse {
    val pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending())
    return repos.findByParticipantsContaining(userId, pageable).let {
      ChatRoomPageResponse(
        it.content.map(ChatRoomResponse::fromChatRoom),
        it.totalElements
      )
    }
  }

  @OptIn(ExperimentalStdlibApi::class)
  fun joinChatRoom(chatRoomId: String, password: String?, userId: String): List<ChatData> {
    val chatRoom = repos.findById(ObjectId(chatRoomId))
      .getOrElse { throw ResponseStatusException(HttpStatusCode.valueOf(400), "Chat room not found") }

    if (chatRoom.privateRoom) {
      if (password.isNullOrBlank()) {
        throw ResponseStatusException(HttpStatusCode.valueOf(400), "Password is required")
      }
      if (!passwordEncoder.matches(password, chatRoom.password)) {
        throw ResponseStatusException(HttpStatusCode.valueOf(400), "Wrong password")
      }
    }

    if (chatRoom.participants.contains(userId)) {
      throw ResponseStatusException(HttpStatusCode.valueOf(400), "Already joined")
    }

    chatRoom.participants.add(userId)
    repos.save(chatRoom)

    val pageable = PageRequest.of(0, DEFAULT_CHAT_COUNT, Sort.by("createdAt").descending())
    return messageRepos.findByRoomId(ObjectId(chatRoomId), pageable).map {
      ChatData.fromMessage(it)
    }
  }
}
