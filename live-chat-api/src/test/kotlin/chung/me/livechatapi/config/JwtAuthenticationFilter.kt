package chung.me.livechatapi.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.test.context.TestComponent
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@TestComponent
class JwtAuthenticationFilter : OncePerRequestFilter() {

  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain,
  ) {
    val wrapper = SecurityContextHolder.getContext().authentication?.let {
      val principal = it.principal
      if (principal is String) {
        return@let request
      }
      val userId = (principal as User).username
      val wrapper = object : HttpServletRequestWrapper(request) {
        override fun getHeaders(name: String?): Enumeration<String> {
          return if (name == USER_ID) {
            Collections.enumeration(listOf(userId))
          } else {
            super.getHeaders(name)
          }
        }
      }
      wrapper
    }

    filterChain.doFilter(wrapper ?: request, response)
  }
}
