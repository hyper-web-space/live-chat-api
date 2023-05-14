package chung.me.livechatapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@EnableMongoRepositories
@EnableMongoAuditing
class LiveChatApiApplication

fun main(args: Array<String>) {
  runApplication<LiveChatApiApplication>(*args)
}
