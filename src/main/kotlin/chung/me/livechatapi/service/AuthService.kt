package chung.me.livechatapi.service

import chung.me.livechatapi.config.JwtService
import chung.me.livechatapi.controller.AuthenticationResponse
import chung.me.livechatapi.entity.RefreshToken
import chung.me.livechatapi.entity.User
import chung.me.livechatapi.repos.RefreshTokenRepos
import chung.me.livechatapi.repos.UserRepos
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(
  private val userRepos: UserRepos,
  private val passwordEncoder: PasswordEncoder,
  private val jwtService: JwtService,
  private val refreshTokenRepos: RefreshTokenRepos,
) {

  fun register(userId: String, password: String): AuthenticationResponse {
    val existUser = userRepos.findByUserId(userId)

    if (existUser != null) {
      throw ResponseStatusException(HttpStatus.CONFLICT, "userId is duplicated")
    }

    val encodedPassword = passwordEncoder.encode(password)
    val newUser = userRepos.save(User(userId, encodedPassword))
    val accessToken = jwtService.generateAccessToken(newUser)
    val refreshToken = jwtService.generateRefreshToken(newUser)

    refreshTokenRepos.save(RefreshToken(userId, refreshToken))

    return AuthenticationResponse(accessToken, refreshToken)
  }

  fun signin(userId: String, password: String): AuthenticationResponse {
    val user = userRepos.findByUserId(userId) ?: throw ResponseStatusException(
      HttpStatus.BAD_REQUEST,
      "user ID is not exists. : $userId"
    )

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
}
