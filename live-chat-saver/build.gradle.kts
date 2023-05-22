import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  id("org.springframework.boot") version "3.0.5"
  id("io.spring.dependency-management") version "1.1.0"
  kotlin("jvm") version "1.7.22"
  kotlin("plugin.spring") version "1.7.22"
  id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
  id("jacoco")
}

group = "chung.me"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

jacoco {
  toolVersion = "0.8.8"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":live-chat-message"))
  implementation("org.springframework.boot:spring-boot-starter-parent:3.0.5")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
  implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
  developmentOnly("org.springframework.boot:spring-boot-devtools")
  implementation("io.projectreactor.netty:reactor-netty-http")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "17"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks {
  jacocoTestReport {
    reports {
      xml.required.set(true)
      html.required.set(true)
    }
  }
}

tasks.withType<BootJar> {
  archiveFileName.set("${project.name}.jar")
}
