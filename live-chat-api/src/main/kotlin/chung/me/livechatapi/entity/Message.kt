package chung.me.livechatapi.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime

@Document
class Message(
  var roomId: ObjectId,
  var sender: String,
  var content: String,
) {
  @MongoId
  lateinit var id: ObjectId

  @CreatedDate
  lateinit var createdAt: LocalDateTime
}
