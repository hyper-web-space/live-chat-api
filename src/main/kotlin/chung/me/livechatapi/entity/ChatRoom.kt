package chung.me.livechatapi.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime

@Document
class ChatRoom(
  val name: String,
  val creator: String,
  val password: String? = null,
) {
  @MongoId
  lateinit var id: ObjectId

  @CreatedDate
  lateinit var createdAt: LocalDateTime

  var privateRoom: Boolean = password != null
  var participants: List<String> = listOf(creator)
}
