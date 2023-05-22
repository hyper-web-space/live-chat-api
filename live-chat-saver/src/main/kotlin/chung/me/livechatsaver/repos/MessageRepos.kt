package chung.me.livechatsaver.repos

import chung.me.livechatmessage.entity.Message
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface MessageRepos : MongoRepository<Message, ObjectId>
