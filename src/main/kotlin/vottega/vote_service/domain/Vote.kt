package vottega.vote_service.domain

import jakarta.persistence.*
import vottega.vote_service.domain.enum.Status
import vottega.vote_service.domain.enum.VotePaperType
import vottega.vote_service.domain.enum.VoteResult
import vottega.vote_service.dto.room.ParticipantResponseDTO
import vottega.vote_service.exception.VoteForbiddenException
import vottega.vote_service.exception.VoteStatusConflictException
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
  name = "vote", indexes = [
    Index(name = "idx_reserved_start_time", columnList = "reservedStartTime"),
  ]
)
class Vote(
  var agendaName: String,
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

  var voteName: String = voteName
    private set

  var isSecret: Boolean = isSecret
    private set

  @OneToMany(mappedBy = "vote", orphanRemoval = true, cascade = [CascadeType.ALL])
  var votePaperList: MutableList<VotePaper> = mutableListOf()
    private set

  var reservedStartTime: LocalDateTime = reservedStartTime ?: LocalDateTime.now()

  var minParticipantNumber: Int = minParticipantNumber ?: 0

  @Embedded
  @AttributeOverrides(
    AttributeOverride(name = "numerator", column = Column(name = "min_participant_rate_numerator")),
    AttributeOverride(name = "denominator", column = Column(name = "min_participant_rate_denominator"))
  )
  var minParticipantRate: FractionVO = minParticipantRate ?: FractionVO(0, 1)
  var createdAt: LocalDateTime? = null
    private set
  var lastUpdatedAt: LocalDateTime? = null
    private set
  var startedAt: LocalDateTime? = null
    private set
  var finishedAt: LocalDateTime? = null
    private set
  var status: Status = Status.CREATED
    private set

  @Embedded
  @AttributeOverrides(
    AttributeOverride(name = "numerator", column = Column(name = "pass_rate_numerator")),
    AttributeOverride(name = "denominator", column = Column(name = "pass_rate_denominator"))
  )
  var passRate: FractionVO = passRate ?: FractionVO(1, 2)
    private set

  var result: VoteResult = VoteResult.NOT_DECIDED
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
    if (status != Status.CREATED) {
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

  fun startVote(participantList: List<ParticipantResponseDTO>) {
    if (status == Status.CREATED) {
      participantList.filter { it.participantRole.canVote && it.isEntered }.forEach {
        votePaperList.add(VotePaper(it.id, this, it.name))
      }
      if (votePaperList.size < minParticipantNumber || votePaperList.size < minParticipantRate.multiply(participantList.filter { it.isEntered }.size)) {
        throw VoteStatusConflictException("참여자 수가 부족합니다.")
      }
      status = Status.STARTED
      startedAt = LocalDateTime.now()
    } else {
      throw VoteStatusConflictException("이미 시작된 투표입니다.")
    }
  }

  fun endVote() {
    if (status == Status.STARTED) {
      status = Status.ENDED
      finishedAt = LocalDateTime.now()
      val yesNum = votePaperList.count { it.votePaperType == VotePaperType.YES }
      if (yesNum >= passRate.multiply(votePaperList.size)) {
        result = VoteResult.PASSED
      } else {
        result = VoteResult.REJECTED
      }
    } else {
      throw VoteStatusConflictException("아직 시작되지 않은 투표입니다.")
    }
  }

  fun resetVote() {
    if (status == Status.STARTED) {
      votePaperList.clear()
      status = Status.CREATED
    } else {
      throw VoteStatusConflictException("투표가 진행 중이지 않습니다.")
    }
  }

  fun addVotePaper(userId: UUID, voteResultType: VotePaperType): VotePaper {
    if (status != Status.STARTED) {
      throw VoteStatusConflictException("투표가 시작되지 않았습니다.")
    }
    votePaperList.forEach {
      //이미 투표한 사람인지 확인
      if (it.userId == userId && it.votePaperType != VotePaperType.NOT_VOTED) {
        throw VoteStatusConflictException("이미 투표한 사람입니다.")
      } else if (it.userId == userId) {
        it.vote(voteResultType)
        return it
      }
    }
    throw VoteForbiddenException("투표 권한이 없습니다.")
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