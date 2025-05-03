package vottega.vote_service.dto

import vottega.vote_service.domain.FractionVO
import vottega.vote_service.domain.enum.Status
import java.time.LocalDateTime

data class VoteRequestDTO(
  val agendaName: String,
  val voteName: String,
  val passRate: FractionVO,
  val isSecret: Boolean?,
  val reservedStartTime: LocalDateTime?,
  val minParticipantNumber: Int?,
  val minParticipantRate: FractionVO
)

data class VoteStatusRequestDTO(
  val status: Status
)