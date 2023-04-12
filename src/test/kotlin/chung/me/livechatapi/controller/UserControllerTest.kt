package chung.me.livechatapi.controller

import chung.me.livechatapi.SpringMvcMockTestSupport
import chung.me.livechatapi.entity.User
import chung.me.livechatapi.repos.RefreshTokenRepos
import chung.me.livechatapi.repos.UserRepos
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class UserControllerTest(
  private val userRepos: UserRepos,
  private val refreshTokenRepos: RefreshTokenRepos,
) : SpringMvcMockTestSupport() {

  @BeforeEach
  fun setUp() {
    userRepos.deleteAll()
    refreshTokenRepos.deleteAll()
  }

  @Test
  fun `회원 가입 테스트`() {
    val response = performPost("/users/signup", AuthBody("user1", "password123")).andReturn().response
    assertEquals(response.status, HttpStatus.CREATED.value())
    assertNotNull(userRepos.findByUserId("user1"))
  }

  @Test
  fun `회원 가입 아이디 중복 테스트`() {
    userRepos.save(User("user1", "pass"))
    val response = performPost("/users/signup", AuthBody("user1", "password123")).andReturn().response
    assertEquals(response.status, HttpStatus.CONFLICT.value())
  }

  @Test
  fun `회원가입 후 로그인 테스트`() {
    val body = AuthBody("user1", "password123")
    val signupResponse = performPost("/users/signup", body).andReturn().response

    val latch = CountDownLatch(1)
    latch.await(500, TimeUnit.MILLISECONDS)

    val response = performPost("/users/signin", body).andReturn().response
    assertEquals(response.status, HttpStatus.OK.value())
    val (accessToken, refreshToken) = toResult<AuthenticationResponse>(response)
    assertNotNull(accessToken)
    assertNotNull(refreshToken)
    assertEquals(refreshTokenRepos.findByUserId("user1")?.token, refreshToken)
  }

  @Test
  fun `없는 아이디로 로그인 테스트`() {
    val body = AuthBody("user1", "password123")
    val response = performPost("/users/signin", body).andReturn().response
    assertEquals(response.status, HttpStatus.BAD_REQUEST.value())
  }

  @Test
  fun `틀린 비밀번호로 로그인 테스트`() {
    performPost("/users/signup", AuthBody("user1", "password123"))
    val response = performPost("/users/signin", AuthBody("user1", "password")).andReturn().response
    assertEquals(response.status, HttpStatus.UNAUTHORIZED.value())
  }
}
