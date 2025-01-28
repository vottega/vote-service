package vottega.vote_service.dto.room

import java.time.LocalDateTime

data class RoomResponseDTO(
    val id: Long,
    val name: String,
    val ownerId: Long,
    val status: RoomStatus,
    val participants: List<ParticipantResponseDTO>,
    val roles: List<ParticipantRoleDTO>,
    val createdAt: LocalDateTime,
    val lastUpdatedAt: LocalDateTime,
    val startedAt: LocalDateTime?,
    val finishedAt: LocalDateTime?,
)
