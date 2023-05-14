package chung.me.livechatapi.controller

import chung.me.livechatapi.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

const val ACCESS_TOKEN = "ACCESS_TOKEN"
const val REFRESH_TOKEN = "REFRESH_TOKEN"

@RestController
@RequestMapping("/users")
class UserController(
  private val authService: AuthService,
) {

  @Operation(
    responses = [
      ApiResponse(
        responseCode = "201",
        description = "회원 가입 성공",
      ),
      ApiResponse(
        responseCode = "409",
        description = "중복되는 user id",
      ),
    ],
    description = "회원 가입",
  )
  @PostMapping("signup")
  @ResponseStatus(HttpStatus.CREATED)
  fun signup(
    @RequestBody body: AuthBody,
    response: HttpServletResponse,
  ) {
    val (userId, password) = body
    authService.register(userId, password)
  }

  @Operation(
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "로그인 성공",
        content = [
          Content(schema = Schema(implementation = AuthenticationResponse::class), mediaType = "application/json")
        ]
      ),
      ApiResponse(
        responseCode = "400",
        description = "존재하지 않는 user id",
        content = [
          Content(
            schema = Schema(implementation = ResponseErrorEntity::class),
            mediaType = "application/json"
          )
        ]
      ),
      ApiResponse(
        responseCode = "401",
        description = "틀린 비밀번호로 로그인 시도",
        content = [
          Content(
            schema = Schema(implementation = ResponseErrorEntity::class),
            mediaType = "application/json"
          )
        ]
      ),
    ],
    description = "로그인",
  )
  @PostMapping("signin")
  fun signin(
    @RequestBody body: AuthBody,
  ): ResponseEntity<AuthenticationResponse> {
    val (userId, password) = body
    val authenticationResponse = authService.signin(userId, password)
    return ResponseEntity.ok().body(authenticationResponse)
  }

  @Operation(
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "갱신 성공",
        content = [
          Content(schema = Schema(implementation = AuthenticationResponse::class), mediaType = "application/json")
        ]
      ),
      ApiResponse(
        responseCode = "401",
        description = "만료된 refresh 토큰으로 시도하거나, 존재하지 않는 user id 가 담긴 토큰으로 시도",
        content = [
          Content(
            schema = Schema(implementation = ResponseErrorEntity::class),
            mediaType = "application/json"
          )
        ]
      ),
    ],
    description = "access 토큰, refresh 토큰 갱신"
  )
  @PostMapping("refresh")
  fun refresh(
    @RequestBody body: RefreshBody,
  ): ResponseEntity<AuthenticationResponse> {
    return ResponseEntity.ok().body(authService.refresh(body.refreshToken))
  }
}

data class AuthBody(
  val userId: String,
  val password: String,
)

data class AuthenticationResponse(
  val accessToken: String,
  val refreshToken: String,
)

data class RefreshBody(
  val refreshToken: String,
)
