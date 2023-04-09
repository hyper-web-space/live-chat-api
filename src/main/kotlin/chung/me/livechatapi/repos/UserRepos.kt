package chung.me.livechatapi.repos

import chung.me.livechatapi.entity.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepos : MongoRepository<User, ObjectId>
