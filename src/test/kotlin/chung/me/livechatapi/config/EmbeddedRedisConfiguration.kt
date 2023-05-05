package chung.me.livechatapi.config

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import redis.embedded.RedisServer
import java.io.File
import java.util.Objects

@Configuration
class EmbeddedRedisConfiguration {

  @Value("\${spring.data.redis.port}")
  private var port: Int = 0
  private lateinit var redisServer: RedisServer

  @PostConstruct
  fun redisServer() {

    if (isArmMac()) {
      redisServer = RedisServer(
        getRedisFileForArcMac(),
        port
      )
    }

    if (!isArmMac()) {
      redisServer = RedisServer(port)
    }

    redisServer.start()
  }

  private fun isArmMac(): Boolean {
    return Objects.equals(System.getProperty("os.arch"), "aarch64") &&
      Objects.equals(System.getProperty("os.name"), "Mac OS X")
  }

  private fun getRedisFileForArcMac(): File {
    return ClassPathResource("binary/redis/redis-server-6.2.5-mac-arm64").file
  }

  @PreDestroy
  fun stopRedis() {
    redisServer.stop()
  }
}
