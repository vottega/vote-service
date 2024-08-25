package vottega.vote_service.domain

import jakarta.persistence.*
import vottega.room_service.dto.RoomResponseDTO
import vottega.vote_service.domain.enum.VotePaperType
import vottega.vote_service.domain.enum.VoteResultType
import vottega.vote_service.domain.enum.VoteStatus
import vottega.vote_service.exception.VoteStatusConflictException
import java.time.LocalDateTime
import java.util.*

@Entity
class Vote(title: String, val roomId: Long, passRate: FractionVO) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var title: String = title
        private set

    @OneToMany(mappedBy = "vote", orphanRemoval = true, cascade = [CascadeType.ALL])
    var votePaperList: MutableList<VotePaper> = mutableListOf()
        private set
    var createdAt: LocalDateTime? = null
        private set
    var lastUpdatedAt: LocalDateTime? = null
        private set
    var startedAt: LocalDateTime? = null
        private set
    var finishedAt: LocalDateTime? = null
        private set
    var status: VoteStatus = VoteStatus.CREATED
        private set

    var passRate: FractionVO = passRate
        private set

    var result: VoteResultType = VoteResultType.NOT_DECIDED
        private set

    fun startVote(room: RoomResponseDTO) {
        if (status == VoteStatus.CREATED) {
            room.participants.filter { it.canVote }.forEach {
                votePaperList.add(VotePaper(it.id, this))
            }
            status = VoteStatus.STARTED
            startedAt = LocalDateTime.now()
        } else {
            throw VoteStatusConflictException("이미 시작된 투표입니다.")
        }
    }

    fun endVote() {
        if (status == VoteStatus.STARTED) {
            status = VoteStatus.ENDED
            finishedAt = LocalDateTime.now()
            val yesNum = votePaperList.count { it.voteResultType == VotePaperType.YES }
            if (yesNum >= passRate.multipy(votePaperList.size)) {
                result = VoteResultType.PASSED
            } else {
                result = VoteResultType.REJECTED
            }
        } else {
            throw VoteStatusConflictException("아직 시작되지 않은 투표입니다.")
        }
    }

    fun resetVote() {
        if (status == VoteStatus.STARTED) {
            votePaperList.forEach { it.voteResultType = VotePaperType.NOT_VOTED }
        } else {
            throw VoteStatusConflictException("투표가 진행 중이지 않습니다.")
        }
    }

    fun addVotePaper(userId: UUID, voteResultType: VotePaperType) {
        if (status != VoteStatus.STARTED) {
            throw VoteStatusConflictException("투표가 시작되지 않았습니다.")
        }
        votePaperList.forEach() {
            //이미 투표한 사람인지 확인
            if (it.userId == userId && it.voteResultType != VotePaperType.NOT_VOTED) {
                throw VoteStatusConflictException("이미 투표한 사람입니다.")
            } else if (it.userId == userId) {
                it.vote(voteResultType)
                return
            }
        }
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