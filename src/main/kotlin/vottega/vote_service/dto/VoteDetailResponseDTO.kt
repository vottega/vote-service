package vottega.vote_service.dto

import vottega.vote_service.domain.FractionVO
import vottega.vote_service.domain.enum.Status
import vottega.vote_service.domain.enum.VoteResult
import java.time.LocalDateTime
import java.util.*

data class VoteDetailResponseDTO(
  val id: Long,
  val agendaName: String,
  val voteName: String,
  val status: Status,
  val createdAt: LocalDateTime,
  val startedAt: LocalDateTime? = null,
  val finishedAt: LocalDateTime? = null,
  val passRate: FractionVO,
  val minParticipantNumber: Int,
  val minParticipantRate: FractionVO,
  val isSecret: Boolean,
  val result: VoteResult? = null,
  val yesList: List<ParticipantIdNameDTO> = listOf(),
  val noList: List<ParticipantIdNameDTO> = listOf(),
  val abstainList: List<ParticipantIdNameDTO> = listOf(),
)

data class ParticipantIdNameDTO(
  val id: UUID?,
  val name: String,
)
