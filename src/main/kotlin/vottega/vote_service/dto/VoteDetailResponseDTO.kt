package vottega.vote_service.dto

import vottega.vote_service.domain.FractionVO
import vottega.vote_service.domain.enum.VoteResultType
import vottega.vote_service.domain.enum.VoteStatus
import java.time.LocalDateTime

data class VoteDetailResponseDTO(
    val id: Long?,
    val title: String,
    val status : VoteStatus,
    val createdAt : LocalDateTime? = null,
    val startedAt : LocalDateTime? = null,
    val finishedAt : LocalDateTime? = null,
    val passRate : FractionVO,
    val result : VoteResultType? = null,
    val yesList : List<ParticipantResponseDTO> = listOf(),
    val noList : List<ParticipantResponseDTO> = listOf(),
    val abstainList : List<ParticipantResponseDTO> = listOf(),
)
