package chung.me.livechatapi.repos

import chung.me.livechatapi.entity.ChatRoom
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface ChatRoomRepos : MongoRepository<ChatRoom, ObjectId>
