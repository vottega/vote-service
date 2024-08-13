package vottega.vote_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VoteServiceApplication

fun main(args: Array<String>) {
	runApplication<VoteServiceApplication>(*args)
}
