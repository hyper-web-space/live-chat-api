package chung.me.livechatapi.repos

import chung.me.livechatapi.entity.ChatRoom
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface ChatRoomRepos : MongoRepository<ChatRoom, ObjectId> {
  fun findByNameLikeIgnoreCase(name: String, pageable: Pageable): Page<ChatRoom>
  fun findByParticipantsContaining(userId: String, pageable: Pageable): Page<ChatRoom>
  // find creator by objectId
  @Query(value = "{ '_id': ?0 }", fields = "{ '_id': 0, 'creator': 1 }")
  fun findCreatorById(objectId: ObjectId): ChatRoom?
  // check ChatRoom.creator in participants where id = objectId
  @Query(value = "{ '_id': ?0, 'participants': { \$nin: [ ?1 ] } }", exists = true)
  fun isRoomClosed(objectId: ObjectId, creator: String): Boolean
}
