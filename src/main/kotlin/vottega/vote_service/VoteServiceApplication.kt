package vottega.vote_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class VoteServiceApplication

fun main(args: Array<String>) {
	runApplication<VoteServiceApplication>(*args)
}
