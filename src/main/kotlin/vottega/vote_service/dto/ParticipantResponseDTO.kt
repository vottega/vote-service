package vottega.vote_service.dto

import java.util.UUID

data class ParticipantResponseDTO(
    val id : UUID,
    val name : String,
)
