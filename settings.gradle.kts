include("live-chat-api")
include("live-chat-saver")
include("live-chat-message")

pluginManagement {

  resolutionStrategy {
    eachPlugin {
      when (requested.id.id) {
        "org.springframework.boot" -> {
          useModule("org.springframework.boot:spring-boot-gradle-plugin:${requested.version}")
        }
      }
    }
  }
}
