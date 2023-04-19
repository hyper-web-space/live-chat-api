package chung.me.livechatapi.service

import chung.me.livechatapi.controller.ChatRoomPageResponse
import chung.me.livechatapi.controller.ChatRoomResponse
import chung.me.livechatapi.controller.CreationChatRoomResponse
import chung.me.livechatapi.entity.ChatRoom
import chung.me.livechatapi.repos.ChatRoomRepos
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class ChatRoomService(
  private val repos: ChatRoomRepos,
  private val passwordEncoder: PasswordEncoder,
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
}
