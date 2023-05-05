package chung.me.livechatapi.controller

import chung.me.livechatapi.SpringMvcMockTestSupport
import chung.me.livechatapi.config.JwtService
import chung.me.livechatapi.entity.User
import chung.me.livechatapi.repos.UserRepos
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.Transport
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.reflect.KClass

class ChattingControllerTest(
  private val jwtService: JwtService,
  private val userRepos: UserRepos,
) : SpringMvcMockTestSupport() {

  @BeforeEach
  fun setUp() {
    userRepos.deleteAll()
  }

  @Test
  fun `웹소켓으로 메시지 전송 테스트`() {
    val webSocketClient = getWebSocketClient()
    val user1Id = "user1"
    val user2Id = "user2"
    userRepos.saveAll(listOf(User(user1Id, ""), User(user2Id, "")))

    val session1 = getStompSession(webSocketClient, user1Id)
    val session2 = getStompSession(webSocketClient, user2Id)
    val anotherSession = getStompSession(webSocketClient, user1Id)

    val room1Queue: BlockingQueue<ChatData> = LinkedBlockingQueue()
    val room2Queue: BlockingQueue<ChatData> = LinkedBlockingQueue()

    session1.subscribe("/chat/room1", MessageFrameHandler(ChatData::class, room1Queue, jwtService))
    session2.subscribe("/chat/room1", MessageFrameHandler(ChatData::class, room1Queue, jwtService))
    anotherSession.subscribe("/chat/room2", MessageFrameHandler(ChatData::class, room2Queue, jwtService))

    val chatData = ChatData(user1Id, "hello", LocalDateTime.now())
    session1.send("/queue/message/room1", chatData)

    val chatData1 = room1Queue.poll(100, TimeUnit.MILLISECONDS)
    val chatData2 = room1Queue.poll(100, TimeUnit.MILLISECONDS)
    val chatData3 = room2Queue.poll(100, TimeUnit.MILLISECONDS)

    assertEquals(chatData, chatData1)
    assertEquals(chatData, chatData2)
    assertNull(chatData3)
  }

  @Test
  fun `Authoriazation 토큰 없이 웹소켓 연결 테스트`() {
    val webSocketClient = getWebSocketClient()

    assertThrows<TimeoutException> {
      getStompSession(webSocketClient, null)
    }
  }

  private fun getWebSocketClient(): WebSocketStompClient {
    val standardWebSocketClient = StandardWebSocketClient()
    val webSocketTransport = WebSocketTransport(standardWebSocketClient)
    val transports: List<Transport> = Collections.singletonList(webSocketTransport)
    val sockJsClient = SockJsClient(transports)
    return WebSocketStompClient(sockJsClient).apply {
      messageConverter = MappingJackson2MessageConverter().also {
        it.objectMapper = objectMapper
      }
    }
  }

  private fun getStompSession(webSocketStompClient: WebSocketStompClient, userId: String?): StompSession {
    return webSocketStompClient.connectAsync(
      "ws://localhost:$port/ws",
      null,
      getStompHeadersWithAuthorizationToken(userId),
      object : StompSessionHandlerAdapter() {}
    )[3, TimeUnit.SECONDS]
  }

  private fun getStompHeadersWithAuthorizationToken(userId: String?): StompHeaders {
    val stompHeaders = StompHeaders()

    if (userId.isNullOrBlank()) {
      return stompHeaders
    }

    val authentication = UsernamePasswordAuthenticationToken(
      User(userId, ""), null, listOf(SimpleGrantedAuthority("MEMBER"))
    )
    val principal = authentication.principal as User
    val token = jwtService.generateAccessToken(principal)

    stompHeaders.add("Authorization", token)
    return stompHeaders
  }
}

class MessageFrameHandler<T : Any>(
  private val kClass: KClass<T>,
  private val queue: BlockingQueue<T>,
  private val jwtService: JwtService,
) : StompSessionHandlerAdapter() {

  override fun getPayloadType(headers: StompHeaders): Type {
    return kClass.java
  }

  override fun handleFrame(headers: StompHeaders, payload: Any?) {
    queue.offer(payload as T)
  }

  override fun handleException(
    session: StompSession,
    command: StompCommand?,
    headers: StompHeaders,
    payload: ByteArray,
    exception: Throwable,
  ) {
    exception.printStackTrace()
  }

  override fun handleTransportError(session: StompSession, exception: Throwable) {
    exception.printStackTrace()
  }
}
