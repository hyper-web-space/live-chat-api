package chung.me.livechatapi.service

import chung.me.livechatapi.config.JwtService
import chung.me.livechatapi.controller.AuthenticationResponse
import chung.me.livechatapi.entity.RefreshToken
import chung.me.livechatapi.entity.User
import chung.me.livechatapi.repos.RefreshTokenRepos
import chung.me.livechatapi.repos.UserRepos
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(
  private val userRepos: UserRepos,
  private val passwordEncoder: PasswordEncoder,
  private val jwtService: JwtService,
  private val refreshTokenRepos: RefreshTokenRepos,
  private val userService: UserService,
) {

  fun register(userId: String, password: String) {
    require(userId.isNotBlank()) { "userId is blank" }
    require(password.isNotBlank()) { "password is blank" }

    val existUser = userRepos.findByUserId(userId)

    if (existUser != null) {
      throw ResponseStatusException(HttpStatus.CONFLICT, "userId is duplicated")
    }

    val encodedPassword = passwordEncoder.encode(password)
    userRepos.save(User(userId, encodedPassword))
  }

  fun signin(userId: String, password: String): AuthenticationResponse {
    require(userId.isNotBlank()) { "userId is blank" }
    require(password.isNotBlank()) { "password is blank" }

    val user = userService.findUser(userId)

    val matches = passwordEncoder.matches(password, user.password)

    if (!matches) {
      throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password.")
    }

    val newAccessToken = jwtService.generateAccessToken(user)
    val newRefreshToken = jwtService.generateRefreshToken(user)

    val refreshToken = refreshTokenRepos.findByUserId(userId)

    if (refreshToken == null) {
      refreshTokenRepos.save(RefreshToken(userId, newRefreshToken))
    } else {
      refreshToken.token = newRefreshToken
      refreshTokenRepos.save(refreshToken)
    }

    return AuthenticationResponse(newAccessToken, newRefreshToken)
  }

  fun refresh(token: String): AuthenticationResponse {
    val userId = jwtService.extractUserId(token)
    val refreshToken = refreshTokenRepos.findByUserId(userId) ?: throw ResponseStatusException(
      HttpStatus.UNAUTHORIZED,
      "refreshToken expired"
    )

    val user = userService.findUser(userId)

    val newAccessToken = jwtService.generateAccessToken(user)
    val newRefreshToken = jwtService.generateRefreshToken(user)
    refreshToken.updateToken(newRefreshToken)
    refreshTokenRepos.save(refreshToken)

    return AuthenticationResponse(newAccessToken, newRefreshToken)
  }

  fun getUsernamePasswordAuthenticationToken(token: String): UsernamePasswordAuthenticationToken {
    val userId = jwtService.extractUserId(token)
    val user = userService.findUser(userId)
    return UsernamePasswordAuthenticationToken(user, null, user.authorities)
  }
}
