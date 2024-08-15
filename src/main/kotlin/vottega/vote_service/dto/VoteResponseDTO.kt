package vottega.vote_service.dto

import vottega.vote_service.domain.enum.VoteResultType
import vottega.vote_service.domain.enum.VoteStatus
import java.time.LocalDateTime

data class VoteResponseDTO(
    val title: String,
    val status : VoteStatus,
    val yesNum : Int,
    val noNum : Int,
    val abstainNum : Int,
    val createdAt : LocalDateTime? = null,
    val result : VoteResultType? = null
)
