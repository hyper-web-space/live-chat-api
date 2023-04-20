package chung.me.livechatapi.repos

import chung.me.livechatapi.entity.ChatRoom
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface ChatRoomRepos : MongoRepository<ChatRoom, ObjectId> {
  fun findByNameLikeIgnoreCase(name: String, pageable: Pageable): Page<ChatRoom>
  fun findByParticipantsContaining(userId: String, pageable: Pageable): Page<ChatRoom>
}
