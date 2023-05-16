package chung.me.livechatapi.repos

import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestPropertySource
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@TestPropertySource(properties = ["de.flapdoodle.mongodb.embedded.version=3.6.5"])
@DataMongoTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class MessageReposTest(
  private val messageRepos: MessageRepos,
) {

  @Test
  fun testSave() {
    val roomId = ObjectId(Date.from(Instant.now()))
    val message = Message(roomId, "sender", "message123")
    val now = LocalDateTime.now()
    val savedMessage = messageRepos.save(message)

    assertThat(savedMessage.id).isEqualTo(message.id)
    assertThat(savedMessage.createdAt).isAfter(now)
  }

  @Test
  fun testFindById() {
    val roomId = ObjectId(Date.from(Instant.now()))
    val message = Message(roomId, "sender", "message123")
    messageRepos.save(message)

    val messageOptional = messageRepos.findById(message.id)
    assertThat(messageOptional.isPresent).isTrue()
    assertThat(messageOptional.get().sender)
      .isEqualTo(message.sender)
  }
}
