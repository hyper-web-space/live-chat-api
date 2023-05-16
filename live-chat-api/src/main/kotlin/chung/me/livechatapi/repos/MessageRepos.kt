package chung.me.livechatapi.repos

import chung.me.livechatmessage.entity.Message
import org.bson.types.ObjectId
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.repository.MongoRepository

interface MessageRepos : MongoRepository<Message, ObjectId> {
  fun findByRoomId(chatRoomId: ObjectId, pageable: PageRequest): List<Message>
}
