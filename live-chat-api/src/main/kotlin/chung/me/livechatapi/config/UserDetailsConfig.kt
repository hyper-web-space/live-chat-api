package chung.me.livechatapi.config

import chung.me.livechatapi.repos.UserRepos
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

@Configuration
class UserDetailsConfig(
  private val userRepos: UserRepos,
) {

  @Bean
  fun userDetailsService(): UserDetailsService {
    return UserDetailsService { userId ->
      userRepos.findByUserId(userId) ?: throw UsernameNotFoundException("User ($userId) not found")
    }
  }
}
