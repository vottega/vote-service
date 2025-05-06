package vottega.vote_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableScheduling
class VoteServiceApplication

fun main(args: Array<String>) {
  runApplication<VoteServiceApplication>(*args)
}
