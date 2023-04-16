package chung.me.livechatapi.repos

import chung.me.livechatapi.entity.ChatRoom
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestPropertySource
import java.time.LocalDateTime

@TestPropertySource(properties = ["de.flapdoodle.mongodb.embedded.version=3.6.5"])
@DataMongoTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ChatRoomReposTest(
  private val chatRoomRepos: ChatRoomRepos,
) {

  @Test
  fun testSave() {
    val chatRoom = ChatRoom("chatRoom", "user1")
    val now = LocalDateTime.now()
    val savedChatRoom = chatRoomRepos.save(chatRoom)

    assertThat(savedChatRoom.id).isEqualTo(chatRoom.id)
    assertThat(savedChatRoom.createdAt).isAfter(now)
    assertThat(savedChatRoom.creator).isEqualTo(chatRoom.creator)
  }

  @Test
  fun testFindById() {
    val chatRoom = ChatRoom("chatRoom", "user1")
    chatRoomRepos.save(chatRoom)
    val chatRoomOptional = chatRoomRepos.findById(chatRoom.id)
    assertThat(chatRoomOptional.isPresent).isTrue()
    assertThat(chatRoomOptional.get().name)
      .isEqualTo(chatRoom.name)
  }
}
