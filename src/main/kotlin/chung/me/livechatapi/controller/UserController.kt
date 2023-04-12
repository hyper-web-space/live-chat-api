package chung.me.livechatapi.controller

import chung.me.livechatapi.service.AuthService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
  private val authService: AuthService,
) {

  @PostMapping("signup")
  @ResponseStatus(HttpStatus.CREATED)
  fun signup(
    @RequestBody body: AuthBody,
    response: HttpServletResponse,
  ) {
    val (userId, password) = body
    authService.register(userId, password)
  }

  @PostMapping("signin")
  fun signin(
    @RequestBody body: AuthBody,
  ): ResponseEntity<AuthenticationResponse> {
    val (userId, password) = body
    val authenticationResponse = authService.signin(userId, password)
    return ResponseEntity.ok().body(authenticationResponse)
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
