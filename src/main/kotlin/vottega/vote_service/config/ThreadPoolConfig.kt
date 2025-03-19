package vottega.vote_service.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
class ThreadPoolConfig {

  @Bean("taskExecutor")
  fun taskExecutor(): Executor {
    val executor = ThreadPoolTaskExecutor()
    executor.corePoolSize = 10
    executor.maxPoolSize = 20
    executor.queueCapacity = 100
    executor.initialize()
    return executor
  }
}