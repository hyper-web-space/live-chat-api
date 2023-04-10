package chung.me.livechatapi.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Encrypted
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime

@Document
class RefreshToken(
  val userId: String,
  @Encrypted var token: String,
) {
  @MongoId
  lateinit var id: ObjectId

  @Indexed(expireAfterSeconds = 604800)
  @CreatedDate
  lateinit var createdAt: LocalDateTime
}
