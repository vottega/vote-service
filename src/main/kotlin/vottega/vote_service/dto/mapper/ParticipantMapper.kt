package vottega.vote_service.dto.mapper

import org.springframework.stereotype.Component
import vottega.avro.ParticipantAvro
import vottega.sse_server.dto.ParticipantResponseDTO
import java.time.ZoneId

@Component
class ParticipantMapper(
  private val participantRoleMapper: ParticipantRoleMapper,
) {
  fun toParticipantResponseDTO(participant: ParticipantAvro): ParticipantResponseDTO {
    return ParticipantResponseDTO(
      id = participant.id,
      name = participant.name,
      roomId = participant.roomId,
      position = participant.position,
      participantRole = participant.role?.let { participantRoleMapper.toParticipantRoleDTO(it) },
      isEntered = participant.isEntered,
      createdAt = participant.createdAt.atZone(ZoneId.systemDefault()).toLocalDateTime(),
      enteredAt = participant.enteredAt?.atZone(ZoneId.systemDefault())?.toLocalDateTime(),
      lastUpdatedAt = participant.lastUpdatedAt?.atZone(ZoneId.systemDefault())?.toLocalDateTime(),
    )
  }
}