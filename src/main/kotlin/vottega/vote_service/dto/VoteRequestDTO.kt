package vottega.vote_service.dto

import vottega.vote_service.domain.FractionVO
import java.time.LocalDateTime

data class VoteRequestDTO(
  val agendaName: String,
  val voteName: String,
  val minPassRate: FractionVO,
  val isSecret: Boolean?,
  val reservedStartTime: LocalDateTime?,
  val minParticipantNumber: Int?,
  val minParticipantRate: FractionVO
)
