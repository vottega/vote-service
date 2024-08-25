package vottega.room_service.dto

import java.time.LocalDateTime

data class RoomResponseDTO(
    val id: Long,
    val name: String,
    val participants: List<ParticipantDetailResponseDTO>,
    val createdAt: LocalDateTime,
    val lastUpdatedAt: LocalDateTime,
    val startedAt: LocalDateTime?,
    val finishedAt: LocalDateTime?,
)
