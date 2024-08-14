package vottega.vote_service.domain

import jakarta.persistence.*
import vottega.vote_service.domain.enum.VoteStatus
import java.time.LocalDateTime

@Entity
class Vote (title : String, val roomId: Long) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null


    var title: String = title
        private set

    @OneToMany(mappedBy = "vote", orphanRemoval = true, cascade = [CascadeType.ALL])
    var votePapers: MutableList<VotePaper> = mutableListOf()
        private set
    var createdAt : LocalDateTime? = null
        private set
    var startTime : LocalDateTime? = null
        private set
    var endTime : LocalDateTime? = null
        private set
    var status : VoteStatus = VoteStatus.CREATED
        private set
}