package chung.me.livechatapi.controller

import chung.me.livechatapi.SpringMvcMockTestSupport
import chung.me.livechatapi.entity.ChatRoom
import chung.me.livechatapi.entity.Message
import chung.me.livechatapi.repos.ChatRoomRepos
import chung.me.livechatapi.repos.MessageRepos
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ChatRoomControllerTest(
  private val chatRoomRepos: ChatRoomRepos,
  private val messageRepos: MessageRepos,
  private val passwordEncoder: PasswordEncoder,
) : SpringMvcMockTestSupport() {

  @BeforeEach
  fun setUp() {
    chatRoomRepos.deleteAll()
    messageRepos.deleteAll()
  }

  @WithMockUser(username = "user1", roles = ["MEMBER"])
  @CsvSource(
    value = [
      "password",
      ","
    ]
  )
  @ParameterizedTest
  fun `채팅방 생성 테스트`(password: String?) {
    val response = performPost("/chatrooms", CreationChatRoomBody("room1", password)).andReturn().response
    assertEquals(response.status, 200)
    val creationChatRoomResponse = toResult<CreationChatRoomResponse>(response)
    assertThat(creationChatRoomResponse)
      .extracting("name", "creator", "privateRoom")
      .containsExactly("room1", "user1", password != null)
  }

  @WithAnonymousUser
  @Test
  fun `권한 없는 접근으로 채팅방 생성 테스트`() {
    val response = performPost("/chatrooms", CreationChatRoomBody("room1")).andReturn().response
    assertEquals(response.status, 401)
  }

  @WithMockUser(username = "user1", roles = ["MEMBER"])
  @Test
  fun `채팅방 조회 테스트`() {
    listOf(
      ChatRoom("room1", "user1", null),
      ChatRoom("room2", "user2", null),
      ChatRoom("roomchat1", "user2", null),
      ChatRoom("CHAT1", "user3", null),
      ChatRoom("CHAT2", "user3", null),
      ChatRoom("CHAT3", "user3", null),
      ChatRoom("chat1", "user3", null),
      ChatRoom("chat2", "user3", null),
      ChatRoom("chat3", "user3", null),
      ChatRoom("chat4", "user3", null),
      ChatRoom("chat5", "user3", null),
      ChatRoom("chatRoom1", "user1", null),
      ChatRoom("chatRoom2", "user1", null),
    ).forEach {
      CountDownLatch(1).await(10, TimeUnit.MILLISECONDS)
      chatRoomRepos.save(it)
    }

    val response = performGet(
      "/chatrooms",
      mapOf(
        "offset" to "1", "limit" to "5", "name" to "chat"
      )
    ).andReturn().response
    assertEquals(response.status, 200)
    val chatRoomPageResponse = toResult<ChatRoomPageResponse>(response)
    assertAll({
      assertThat(chatRoomPageResponse.total)
        .isEqualTo(11L)
      assertThat(chatRoomPageResponse.chatRooms)
        .extracting("name")
        .containsExactly(
          "chat2",
          "chat1",
          "CHAT3",
          "CHAT2",
          "CHAT1",
        )
    })
  }

  @WithMockUser(username = "user1", roles = ["MEMBER"])
  @Test
  fun `참가중인 채팅방 조회 테스트`() {

    listOf(
      ChatRoom("chat1", "user3", null).apply { participants.add("user1") },
      ChatRoom("chat2", "user1", null),
      ChatRoom("chat3", "user1", null),
      ChatRoom("chat4", "user1", null),
      ChatRoom("chat5", "user3", null),
      ChatRoom("chatRoom1", "user1", null),
      ChatRoom("chatRoom2", "user1", null),
    ).forEach {
      CountDownLatch(1).await(10, TimeUnit.MILLISECONDS)
      chatRoomRepos.save(it)
    }

    val response = performGet(
      "/chatrooms/connected",
      mapOf(
        "offset" to "0", "limit" to "3",
      )
    ).andReturn().response
    assertEquals(response.status, 200)
    val chatRoomPageResponse = toResult<ChatRoomPageResponse>(response)
    assertAll({
      assertThat(chatRoomPageResponse.total)
        .isEqualTo(6L)
      assertThat(chatRoomPageResponse.chatRooms)
        .extracting("name")
        .containsExactly(
          "chatRoom2",
          "chatRoom1",
          "chat4",
        )
    })
  }

  @WithMockUser(username = "user3", roles = ["MEMBER"])
  @Test
  fun `채팅방 입장 테스트`() {
    val chatRoom = chatRoomRepos.save(ChatRoom("room1", "user1", null))
    val roomId = chatRoom.id
    messageRepos.saveAll(
      listOf(
        Message(roomId, "user1", "message1"),
        Message(roomId, "user2", "message2"),
        Message(roomId, "user1", "message3"),
        Message(roomId, "user2", "message4"),
        Message(roomId, "user1", "message5"),
      )
    )
    val response = performPost("/chatrooms/$roomId", JoinChatRoomBody()).andReturn().response
    assertEquals(response.status, 200)
    val joinChatRoomResponse = toResult<List<ChatData>>(response)
    assertThat(joinChatRoomResponse)
      .extracting("sender", "contents")
      .containsExactlyInAnyOrder(
        tuple("user1", "message1"),
        tuple("user2", "message2"),
        tuple("user1", "message3"),
        tuple("user2", "message4"),
        tuple("user1", "message5"),
      )
  }

  @WithMockUser(username = "user1", roles = ["MEMBER"])
  @Test
  fun `이미 입장된 채팅방 입장 테스트`() {
    val chatRoom = chatRoomRepos.save(ChatRoom("room1", "user1", null))
    val roomId = chatRoom.id
    val response = performPost("/chatrooms/$roomId", JoinChatRoomBody()).andReturn().response
    assertEquals(response.status, 400)
    val errorResponse = toResult<ResponseErrorEntity>(response)
    val code = HttpStatus.BAD_REQUEST.toString()
    assertThat(errorResponse)
      .isEqualTo(ResponseErrorEntity(code, "Already joined"))
  }

  @WithMockUser(username = "user3", roles = ["MEMBER"])
  @Test
  fun `비공개 채팅방 입장 테스트`() {
    val chatRoom = chatRoomRepos.save(ChatRoom("room1", "user1", passwordEncoder.encode("1234")))
    val roomId = chatRoom.id
    messageRepos.saveAll(
      listOf(
        Message(roomId, "user1", "message1"),
        Message(roomId, "user2", "message2"),
        Message(roomId, "user1", "message3"),
        Message(roomId, "user2", "message4"),
        Message(roomId, "user1", "message5"),
      )
    )
    val response = performPost("/chatrooms/$roomId", JoinChatRoomBody("1234")).andReturn().response
    assertEquals(response.status, 200)
    val joinChatRoomResponse = toResult<List<ChatData>>(response)
    assertThat(joinChatRoomResponse)
      .extracting("sender", "contents")
      .containsExactlyInAnyOrder(
        tuple("user1", "message1"),
        tuple("user2", "message2"),
        tuple("user1", "message3"),
        tuple("user2", "message4"),
        tuple("user1", "message5"),
      )
  }

  @WithMockUser(username = "user3", roles = ["MEMBER"])
  @ParameterizedTest
  @CsvSource(
    value = [
      ",",
      "'   ',"
    ]
  )
  fun `빈 암호로 비공개 채팅방 입장 테스트`(password: String?) {
    val chatRoom = chatRoomRepos.save(ChatRoom("room1", "user1", passwordEncoder.encode("1234")))
    val roomId = chatRoom.id
    val response = performPost("/chatrooms/$roomId", JoinChatRoomBody(password)).andReturn().response
    assertEquals(response.status, 400)
    val errorResponse = toResult<ResponseErrorEntity>(response)
    val code = HttpStatus.BAD_REQUEST.toString()
    assertThat(errorResponse)
      .isEqualTo(ResponseErrorEntity(code, "Password is required"))
  }

  @WithMockUser(username = "user3", roles = ["MEMBER"])
  @ParameterizedTest
  @CsvSource(
    value = [
      "password",
      "1234",
      "Pass1234",
      "PASS1234",
    ]
  )
  fun `틀린 암호로 비공개 채팅방 입장 테스트`(password: String?) {
    val chatRoom = chatRoomRepos.save(ChatRoom("room1", "user1", passwordEncoder.encode("pass1234")))
    val roomId = chatRoom.id
    val response = performPost("/chatrooms/$roomId", JoinChatRoomBody(password)).andReturn().response
    assertEquals(response.status, 400)
    val errorResponse = toResult<ResponseErrorEntity>(response)
    val code = HttpStatus.BAD_REQUEST.toString()
    assertThat(errorResponse)
      .isEqualTo(ResponseErrorEntity(code, "Wrong password"))
  }

  @WithMockUser(username = "user3", roles = ["MEMBER"])
  @Test
  fun `존재하지 않는 채팅방 입장 테스트`() {
    val roomId = "64429dd3e31ecb7d2ef092e2"
    val response = performPost("/chatrooms/$roomId", JoinChatRoomBody()).andReturn().response
    assertEquals(response.status, 400)
    val errorResponse = toResult<ResponseErrorEntity>(response)
    val code = HttpStatus.BAD_REQUEST.toString()
    assertThat(errorResponse)
      .isEqualTo(ResponseErrorEntity(code, "Chat room not found"))
  }
}
