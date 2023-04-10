package chung.me.livechatapi.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*
import java.util.function.Function

@Service
class JwtService(
  @Value("\${spring.server.secretKey}") private val secretKey: String,
) {

  fun extractLoginId(token: String): String {
    return extractClaim(token, Claims::getSubject)
  }

  private fun <T> extractClaim(token: String, claimsResolver: Function<Claims, T>): T {
    val claims = extractAllClaims(token)
    return claimsResolver.apply(claims)
  }

  fun generateAccessToken(userDetails: UserDetails): String {
    return generateToken(emptyMap(), userDetails, 60 * 60 * 2)
  }

  fun generateRefreshToken(userDetails: UserDetails): String {
    return generateToken(emptyMap(), userDetails, 60 * 60 * 24 * 7)
  }

  private fun generateToken(
    extraClaims: Map<String, Any>,
    userDetails: UserDetails,
    expirationSeconds: Long,
  ): String {
    return Jwts
      .builder()
      .setClaims(extraClaims)
      .setSubject(userDetails.username)
      .setIssuedAt(Date(System.currentTimeMillis()))
      .setExpiration(Date(System.currentTimeMillis() + 1000 * expirationSeconds))
      .signWith(getSignInKey(), SignatureAlgorithm.HS256)
      .compact()
  }

  fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
    val loginId = extractLoginId(token)
    return loginId == userDetails.username && !isTokenExpired(token)
  }

  private fun isTokenExpired(token: String): Boolean {
    return extractExpiration(token).before(Date())
  }

  private fun extractExpiration(token: String): Date {
    return extractClaim(token, Claims::getExpiration)
  }

  private fun extractAllClaims(token: String): Claims {
    return Jwts
      .parserBuilder()
      .setSigningKey(getSignInKey())
      .build()
      .parseClaimsJws(token)
      .body
  }

  private fun getSignInKey(): Key {
    val keyBytes = Decoders.BASE64.decode(secretKey)
    return Keys.hmacShaKeyFor(keyBytes)
  }
}
