package chung.me.livechatapi.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

@Document
class User(
  var userId: String,
  private val password: String,
) : UserDetails {
  @MongoId
  lateinit var id: ObjectId

  @CreatedDate
  lateinit var createdAt: LocalDateTime
  private var role: Role = Role.MEMBER
  override fun getAuthorities(): List<GrantedAuthority> {
    return listOf(SimpleGrantedAuthority(role.name))
  }

  override fun getPassword(): String {
    return password
  }

  override fun getUsername(): String {
    return userId
  }

  override fun isAccountNonExpired(): Boolean {
    return true
  }

  override fun isAccountNonLocked(): Boolean {
    return true
  }

  override fun isCredentialsNonExpired(): Boolean {
    return true
  }

  override fun isEnabled(): Boolean {
    return true
  }
}

enum class Role(key: String, title: String) {
  MEMBER("MEMBER", "회원"),
}
