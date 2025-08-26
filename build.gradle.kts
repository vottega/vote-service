plugins {
  kotlin("jvm") version "1.9.24"
  kotlin("plugin.spring") version "1.9.24"
  id("org.springframework.boot") version "3.3.2"
  id("io.spring.dependency-management") version "1.1.6"
  id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
  kotlin("plugin.jpa") version "1.9.24"
}

group = "vottega"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

ext {
  set("springCloudVersion", "2023.0.3")
}

dependencyManagement {
  imports {
    mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
  }
}

repositories {
  maven {
    url = uri("https://maven.pkg.github.com/vottega/security")
    credentials {
      username = findProperty("gpr.user") as String?
        ?: System.getenv("GITHUB_ACTOR")
      password = findProperty("gpr.key") as String?
        ?: System.getenv("GITHUB_TOKEN")
    }
  }
  mavenCentral()
  maven("https://packages.confluent.io/maven/")
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-data-redis")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
  runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.springframework.kafka:spring-kafka")
  compileOnly("org.projectlombok:lombok")
  runtimeOnly("com.mysql:mysql-connector-j")
  annotationProcessor("org.projectlombok:lombok")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testImplementation("org.springframework.kafka:spring-kafka-test")
  testImplementation("org.springframework.security:spring-security-test")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  implementation("vottega:security-starter:1.1.2")

  implementation("org.apache.avro:avro:1.11.4")
  implementation("io.confluent:kafka-avro-serializer:7.5.0")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
  implementation("io.swagger.core.v3:swagger-annotations:2.2.20")

  testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
  testImplementation("org.mockito:mockito-core:4.8.0")
  testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")

  testImplementation("org.testcontainers:testcontainers:1.20.6")
  testImplementation("org.testcontainers:junit-jupiter:1.20.6")
  implementation("com.h2database:h2")
  implementation("org.redisson:redisson-spring-boot-starter:3.31.0")
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
