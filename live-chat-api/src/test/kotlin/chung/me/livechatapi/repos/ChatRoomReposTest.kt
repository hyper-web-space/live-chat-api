package chung.me.livechatapi.repos

import chung.me.livechatapi.entity.ChatRoom
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestPropertySource
import java.time.LocalDateTime
import java.util.*

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

  @Test
  fun `id 로 creator 찾기`() {
    val chatRoom = ChatRoom("chatRoom", "user1")
    chatRoomRepos.save(chatRoom)
    val creator = chatRoomRepos.findCreatorById(chatRoom.id)?.creator
    assertThat(creator).isEqualTo(chatRoom.creator)
  }

  @Test
  fun `없는 id 로 creator 찾기`() {
    val creator = chatRoomRepos.findCreatorById(ObjectId(Date()))
    assertThat(creator).isNull()
  }

  @Test
  fun `id 로 방이 닫혔는지 확인하기 - 닫힌 경우`() {
    val chatRoom = ChatRoom("chatRoom", "user1")
    chatRoom.participants.remove("user1")
    chatRoomRepos.save(chatRoom)

    val isRoomClosed = chatRoomRepos.isRoomClosed(chatRoom.id, chatRoom.creator)
    assertThat(isRoomClosed).isTrue()
  }

  @Test
  fun `id 로 방이 닫혔는지 확인하기 - 열린 경우`() {
    val chatRoom = ChatRoom("chatRoom", "user1")
    chatRoomRepos.save(chatRoom)
    val isRoomClosed = chatRoomRepos.isRoomClosed(chatRoom.id, chatRoom.creator)
    assertThat(isRoomClosed).isFalse()
  }
}
