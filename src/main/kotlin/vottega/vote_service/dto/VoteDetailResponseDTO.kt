package vottega.vote_service.dto

import vottega.vote_service.domain.FractionVO
import vottega.vote_service.domain.enum.Status
import vottega.vote_service.domain.enum.VoteResult
import java.time.LocalDateTime

data class VoteDetailResponseDTO(
  val id: Long,
  val roomId: Long,
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
  val votePaperList: List<VotePaperDTO> = listOf(),
)
