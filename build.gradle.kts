plugins {
  id("org.springframework.boot") version "3.0.5"
  id("io.spring.dependency-management") version "1.1.0"
  kotlin("jvm") version "1.7.22"
  id("jacoco-report-aggregation")
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":live-chat-api"))
  implementation(project(":live-chat-saver"))
}

tasks.check {
  dependsOn(tasks.named<JacocoReport>("testCodeCoverageReport"))
}
