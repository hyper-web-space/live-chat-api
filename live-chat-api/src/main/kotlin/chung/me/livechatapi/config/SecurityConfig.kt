package chung.me.livechatapi.config

import chung.me.livechatapi.entity.Role
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
  private val jwtAuthFilter: JwtAuthenticationFilter,
  private val userDetailsService: UserDetailsService,
) {

  @Bean
  fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
    httpSecurity.csrf()
      .disable()
      .authorizeHttpRequests { auth ->
        auth
          .requestMatchers(
            "/swagger-ui/**", "/v3/api-docs/**", "/users/signup", "/users/signin", "/users/refresh", "/ws/**"
          ).permitAll()
          .requestMatchers(
            "/chats/**", "/chartrooms/**"
          ).hasAuthority(Role.MEMBER.name)
          .anyRequest()
          .authenticated()
          .and()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          .and()
          .authenticationProvider(authenticationProvider())
          .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
      }
      .exceptionHandling {
        it.authenticationEntryPoint { _, res, _ ->
          res.status = HttpStatus.UNAUTHORIZED.value()
          res.contentType = MediaType.APPLICATION_JSON.type
        }
      }
    return httpSecurity.build()
  }

  @Bean
  fun passwordEncoder(): BCryptPasswordEncoder {
    return BCryptPasswordEncoder()
  }

  @Bean
  fun authenticationProvider(): AuthenticationProvider {
    return DaoAuthenticationProvider()
      .apply {
        this.setUserDetailsService(userDetailsService)
        this.setPasswordEncoder(passwordEncoder())
      }
  }

  @Bean
  fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
    return config.authenticationManager
  }
}
