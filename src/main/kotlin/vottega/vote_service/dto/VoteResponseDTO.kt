package vottega.vote_service.dto

import vottega.vote_service.domain.FractionVO
import vottega.vote_service.domain.enum.Status
import vottega.vote_service.domain.enum.VoteResult
import java.time.LocalDateTime

data class VoteResponseDTO(
  val id: Long,
  val roomId: Long,
  val agendaName: String,
  val voteName: String,
  val status: Status,
  val passRate: FractionVO,
  val minParticipantNumber: Int,
  val minParticipantRate: FractionVO,
  val reservedStartTime: LocalDateTime,
  val isSecret: Boolean,
  val yesNum: Int,
  val noNum: Int,
  val abstainNum: Int,
  val totalNum: Int,
  val createdAt: LocalDateTime,
  val result: VoteResult,
  val startedAt: LocalDateTime? = null,
  val finishedAt: LocalDateTime? = null,
  val lastUpdatedAt: LocalDateTime,
)
