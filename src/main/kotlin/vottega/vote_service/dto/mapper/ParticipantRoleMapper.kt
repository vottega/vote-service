package vottega.vote_service.dto.mapper

import org.springframework.stereotype.Component
import vottega.room_service.avro.ParticipantRoleAvro
import vottega.vote_service.dto.room.ParticipantRoleDTO

@Component
class ParticipantRoleMapper {
  fun toParticipantRoleDTO(role: ParticipantRoleAvro): ParticipantRoleDTO {
    return ParticipantRoleDTO(role.role, role.canVote)
  }
}