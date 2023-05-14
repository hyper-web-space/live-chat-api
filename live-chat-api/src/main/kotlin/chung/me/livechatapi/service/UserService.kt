package chung.me.livechatapi.service

import chung.me.livechatapi.entity.User
import chung.me.livechatapi.repos.UserRepos
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
  private val userRepos: UserRepos,
) {

  @Transactional(readOnly = true)
  fun findUser(userId: String): User {
    return userRepos.findByUserId(userId) ?: throw ResponseStatusException(
      HttpStatus.BAD_REQUEST,
      "user ID is not exists. : $userId"
    )
  }
}
