package vottega.vote_service.dto

import vottega.vote_service.domain.enum.VoteResultType
import vottega.vote_service.domain.enum.VoteStatus
import java.time.LocalDateTime

data class VoteDetailResponseDTO(
    val title: String,
    val status : VoteStatus,
    val createdAt : LocalDateTime,
    val startedAt : LocalDateTime? = null,
    val finishedAt : LocalDateTime? = null,
    val result : VoteResultType? = null,
    val yesList : List<String> = listOf(),
    val noList : List<String> = listOf(),
    val abstainList : List<String> = listOf(),
)
