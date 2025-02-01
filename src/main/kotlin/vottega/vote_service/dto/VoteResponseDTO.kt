package vottega.vote_service.dto

import vottega.vote_service.domain.FractionVO
import vottega.vote_service.domain.enum.VoteResultType
import vottega.vote_service.domain.enum.VoteStatus
import java.time.LocalDateTime

data class VoteResponseDTO(
  val id: Long,
  val agendaName: String,
  val voteName: String,
  val status: VoteStatus,
  val passRate: FractionVO,
  val minParticipantNumber: Int,
  val minParticipantRate: FractionVO,
  val isSecret: Boolean,
  val yesNum: Int,
  val noNum: Int,
  val abstainNum: Int,
  val createdAt: LocalDateTime,
  val result: VoteResultType? = null
)
