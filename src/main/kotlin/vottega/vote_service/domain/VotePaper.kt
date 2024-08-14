package vottega.vote_service.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
class VotePaper (userId : UUID , vote: Vote) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var userId : UUID = userId
        private set

    //TODO 찬 반 기

    @ManyToOne
    @JoinColumn(name = "vote_id")
    var vote : Vote = vote
        private set

    var createdAt : LocalDateTime? = null
        private set
}