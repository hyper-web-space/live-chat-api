package chung.me.livechatapi.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime

@Document
class ChatRoom(
  var name: String,
  var creator: String,
  var password: String? = null,
) {

  @PersistenceCreator
  constructor(creator: String) : this("unknown", creator)

  @MongoId
  lateinit var id: ObjectId

  @CreatedDate
  lateinit var createdAt: LocalDateTime

  var privateRoom: Boolean = password != null
  var participants: MutableSet<String> = mutableSetOf(creator)
}
