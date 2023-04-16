package chung.me.livechatapi.service

import chung.me.livechatapi.controller.CreationChatRoomResponse
import chung.me.livechatapi.entity.ChatRoom
import chung.me.livechatapi.repos.ChatRoomRepos
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
}
