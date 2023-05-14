package chung.me.livechatapi.repos

import chung.me.livechatapi.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestPropertySource
import java.time.LocalDateTime

@TestPropertySource(properties = ["de.flapdoodle.mongodb.embedded.version=3.6.5"])
@DataMongoTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class UserReposTest(
  private val userRepos: UserRepos,
) {

  @Test
  fun testSave() {
    val user = User("user1", "password1")
    val now = LocalDateTime.now()
    val savedUser = userRepos.save(user)

    assertThat(savedUser.id).isEqualTo(user.id)
    assertThat(savedUser.createdAt).isAfter(now)
  }

  @Test
  fun testFindById() {
    val user = User("user1", "password1")
    userRepos.save(user)
    val userOptional = userRepos.findById(user.id)
    assertThat(userOptional.isPresent).isTrue()
    assertThat(userOptional.get().id)
      .isEqualTo(user.id)
  }
}
