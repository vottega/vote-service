package vottega.vote_service.dto.room

import java.time.LocalDateTime
import java.util.UUID

data class ParticipantResponseDTO(
    val id: UUID,
    val name: String,
    val roomId: Long,
    val position: String?,
    val participantRole: ParticipantRoleDTO,
    val isEntered: Boolean,
    val createdAt: LocalDateTime,
    val enteredAt: LocalDateTime?,
    val lastUpdatedAt: LocalDateTime,
)