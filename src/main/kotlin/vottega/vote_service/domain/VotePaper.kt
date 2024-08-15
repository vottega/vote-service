package vottega.vote_service.domain

import jakarta.persistence.*
import vottega.vote_service.domain.enum.VoteResultType
import java.time.LocalDateTime
import java.util.UUID

@Entity
class VotePaper(
    val userId: UUID,
    @ManyToOne @JoinColumn(name = "vote_id") val vote: Vote,
    val voteResultType: VoteResultType
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var createdAt: LocalDateTime? = null
        private set

    @PrePersist
    fun prePersist() {
        createdAt = LocalDateTime.now()
    }
}