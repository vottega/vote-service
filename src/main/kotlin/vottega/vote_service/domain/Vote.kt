package vottega.vote_service.domain

import jakarta.persistence.*
import vottega.vote_service.domain.enum.VoteResultType
import vottega.vote_service.domain.enum.VoteStatus
import java.time.LocalDateTime
import java.util.*

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
    var lastUpdatedAt : LocalDateTime? = null
        private set
    var startedAt : LocalDateTime? = null
        private set
    var finishedAt : LocalDateTime? = null
        private set
    var status : VoteStatus = VoteStatus.CREATED
        private set


    fun startVote() {
        if (status == VoteStatus.CREATED) {
            status = VoteStatus.STARTED
            startedAt = LocalDateTime.now()
        } else {
            throw IllegalStateException("이미 시작된 투표입니다.")
        }
    }

    fun endVote() {
        if (status == VoteStatus.STARTED) {
            status = VoteStatus.ENDED
            finishedAt = LocalDateTime.now()
        } else {
            throw IllegalStateException("아직 시작되지 않은 투표입니다.")
        }
    }

    fun addVotePaper(userId : UUID, voteResultType: VoteResultType) {
        if(status != VoteStatus.STARTED) {
            throw IllegalStateException("투표가 시작되지 않았습니다.")
        }
        //이미 투표한 사람인지 확인
        votePapers.forEach() {
            if (it.userId == userId) {
                throw IllegalStateException("이미 투표한 사람입니다.")
            }
        }

        votePapers.add(VotePaper(userId, this, voteResultType))
    }

    @PrePersist
    fun prePersist() {
        createdAt = LocalDateTime.now()
        lastUpdatedAt = LocalDateTime.now()
    }

    @PreUpdate
    fun preUpdate() {
        lastUpdatedAt = LocalDateTime.now()
    }
}