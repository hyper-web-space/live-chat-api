package chung.me.livechatapi.controller

import chung.me.livechatapi.SpringMvcMockTestSupport
import chung.me.livechatapi.config.JwtService
import chung.me.livechatapi.entity.ChatRoom
import chung.me.livechatapi.entity.User
import chung.me.livechatapi.repos.ChatRoomRepos
import chung.me.livechatapi.repos.UserRepos
import chung.me.livechatmessage.dto.ChatData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpHeaders
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
import java.util.concurrent.ExecutionException
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class ChattingControllerTest(
  private val jwtService: JwtService,
  private val userRepos: UserRepos,
  private val chatRoomRepos: ChatRoomRepos,
) : SpringMvcMockTestSupport() {

  @BeforeEach
  fun setUp() {
    userRepos.deleteAll()
    chatRoomRepos.deleteAll()
  }

  @Test
  fun `웹소켓으로 메시지 전송 테스트`() {
    val webSocketClient = getWebSocketClient()
    val user1Id = "user1"
    val user2Id = "user2"

    val chatRoom = chatRoomRepos.save(ChatRoom("room1", user1Id))
    val chatRoomId = chatRoom.id
    userRepos.saveAll(listOf(User(user1Id, ""), User(user2Id, "")))

    val session1 = getStompSession(webSocketClient, user1Id)
    val session2 = getStompSession(webSocketClient, user2Id)
    val anotherSession = getStompSession(webSocketClient, user1Id)

    val room1Queue: BlockingQueue<ChatData> = LinkedBlockingQueue()
    val room2Queue: BlockingQueue<ChatData> = LinkedBlockingQueue()

    session1.subscribe("/chat/$chatRoomId", MessageFrameHandler(ChatData::class, room1Queue))
    session2.subscribe("/chat/$chatRoomId", MessageFrameHandler(ChatData::class, room1Queue))
    anotherSession.subscribe("/chat/room2", MessageFrameHandler(ChatData::class, room2Queue))

    val chatData = ChatData(user1Id, "hello", LocalDateTime.now())
    session1.send("/queue/message/$chatRoomId", chatData)

    val chatData1 = room1Queue.poll(1, TimeUnit.SECONDS)
    val chatData2 = room1Queue.poll(1, TimeUnit.SECONDS)
    val chatData3 = room2Queue.poll(1, TimeUnit.SECONDS)

    assertEquals(chatData, chatData1)
    assertEquals(chatData, chatData2)
    assertNull(chatData3)
  }

  @Test
  fun `닫힌 채팅 방에 메시지 전송 테스트`() {
    val webSocketClient = getWebSocketClient()
    val user1Id = "user1"
    val user2Id = "user2"

    val chatRoom = ChatRoom("room1", user1Id).apply { participants.remove(user1Id) }
    chatRoomRepos.save(chatRoom)
    val chatRoomId = chatRoom.id
    userRepos.saveAll(listOf(User(user1Id, ""), User(user2Id, "")))

    val session1 = getStompSession(webSocketClient, user1Id)
    val session2 = getStompSession(webSocketClient, user2Id)

    val room1Queue: BlockingQueue<ChatData> = LinkedBlockingQueue()

    session1.subscribe("/chat/$chatRoomId", MessageFrameHandler(ChatData::class, room1Queue))
    session2.subscribe("/chat/$chatRoomId", MessageFrameHandler(ChatData::class, room1Queue))

    val chatData = ChatData(user1Id, "hello", LocalDateTime.now())

    session1.send("/queue/message/$chatRoomId", chatData)
    val chatDataResponse = room1Queue.poll(100, TimeUnit.MILLISECONDS)
    assertNull(chatDataResponse)
  }

  @Test
  fun `Authoriazation 토큰 없이 웹소켓 연결 테스트`() {
    val webSocketClient = getWebSocketClient()

    assertThrows<ExecutionException> {
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

    stompHeaders.add(HttpHeaders.AUTHORIZATION, token)
    return stompHeaders
  }
}

class MessageFrameHandler<T : Any>(
  private val kClass: KClass<T>,
  private val queue: BlockingQueue<T>
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
