package vottega.vote_service.domain

import jakarta.persistence.*
import vottega.vote_service.domain.enum.VotePaperType
import java.time.LocalDateTime
import java.util.UUID

@Entity
class VotePaper(
    val userId: UUID,
    @ManyToOne @JoinColumn(name = "vote_id") val vote: Vote,

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var createdAt: LocalDateTime? = null
        private set
    var votedAt : LocalDateTime? = null
        private set

    var voteResultType: VotePaperType = VotePaperType.NOT_VOTED
        private

    @PrePersist
    fun prePersist() {
        createdAt = LocalDateTime.now()
    }

    fun vote(voteResultType: VotePaperType) {
        this.voteResultType = voteResultType
        this.votedAt = LocalDateTime.now()
    }
}