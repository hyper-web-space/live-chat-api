package chung.me.livechatapi.controller

import chung.me.livechatapi.SpringMvcMockTestSupport
import chung.me.livechatapi.repos.ChatRoomRepos
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser

class ChatRoomControllerTest(
  private val chatRoomRepos: ChatRoomRepos,
) : SpringMvcMockTestSupport() {

  @BeforeEach
  fun setUp() {
    chatRoomRepos.deleteAll()
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
}
