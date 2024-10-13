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
class Vote(
    agendaName: String,
    voteName: String,
    val roomId: Long,
    passRate: FractionVO?,
    isSecret: Boolean,
    reservedStartTime: LocalDateTime?,
    minParticipantNumber: Int?,
    minParticipantRate: FractionVO?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var agendaName: String = agendaName
    var voteName: String = voteName
        private set

    var isSecret: Boolean = isSecret
        private set

    @OneToMany(mappedBy = "vote", orphanRemoval = true, cascade = [CascadeType.ALL])
    var votePaperList: MutableList<VotePaper> = mutableListOf()
        private set

    var reservedStartTime: LocalDateTime = reservedStartTime ?: LocalDateTime.now()

    var minParticipantNumber: Int = minParticipantNumber ?: 0

    var minParticipantRate: FractionVO = minParticipantRate ?: FractionVO(0, 1)
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

    var passRate: FractionVO = passRate ?: FractionVO(1, 2)
        private set

    var result: VoteResultType = VoteResultType.NOT_DECIDED
        private set

    fun update(
        agendaName: String?,
        voteName: String?,
        passRate: FractionVO?,
        isSecret: Boolean?,
        reservedStartTime: LocalDateTime?,
        minParticipantNumber: Int?,
        minParticipantRate: FractionVO?
    ) {
        if (status != VoteStatus.CREATED) {
            throw VoteStatusConflictException("투표가 진행 중이라 수정할 수 없습니다.")
        }
        agendaName?.let { this.agendaName = it }
        voteName?.let { this.voteName = it }
        passRate?.let { this.passRate = it }
        isSecret?.let { this.isSecret = it }
        reservedStartTime?.let { this.reservedStartTime = it }
        minParticipantNumber?.let { this.minParticipantNumber = it }
        minParticipantRate?.let { this.minParticipantRate = it }
    }

    fun startVote(room: RoomResponseDTO) {
        if (status == VoteStatus.CREATED) {
            room.participants.filter { it.canVote }.forEach {
                votePaperList.add(VotePaper(it.id, this))
            }
            if (votePaperList.size < minParticipantNumber && votePaperList.size < minParticipantRate.multipy(room.participants.size)) {
                throw VoteStatusConflictException("참여자 수가 부족합니다.")
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
            status = VoteStatus.CREATED
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