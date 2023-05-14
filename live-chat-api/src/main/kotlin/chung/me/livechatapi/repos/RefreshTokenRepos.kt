package chung.me.livechatapi.repos

import chung.me.livechatapi.entity.RefreshToken
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface RefreshTokenRepos : MongoRepository<RefreshToken, ObjectId> {
  fun findByUserId(userId: String): RefreshToken?
}
