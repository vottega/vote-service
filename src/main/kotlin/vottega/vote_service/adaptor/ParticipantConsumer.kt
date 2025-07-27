package vottega.vote_service.adaptor

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import vottega.avro.Action
import vottega.avro.ParticipantAvro
import vottega.vote_service.dto.mapper.ParticipantMapper
import vottega.vote_service.service.cache.RoomParticipantService

@Component
class ParticipantConsumer(
  private val roomParticipantService: RoomParticipantService,
  private val participantMapper: ParticipantMapper
) {
  @KafkaListener(
    topics = ["participant"],
    groupId = "\${spring.kafka.consumer.group-id}",
    containerFactory = "participantKafkaListenerContainerFactory"
  )
  fun participantConnectEventConsume(participantAvro: ParticipantAvro) {
    when (participantAvro.action) {
      Action.ADD -> {
        participantMapper.toParticipantResponseDTO(participantAvro).let {
          roomParticipantService.addRoomParticipant(it.roomId, it)
        }
      }

      Action.DELETE -> {
        roomParticipantService.deleteRoomParticipant(participantAvro.roomId, participantAvro.id)
      }

      Action.EDIT -> {
        participantMapper.toParticipantResponseDTO(participantAvro).let {
          roomParticipantService.editRoomParticipant(it.roomId, it)
        }
      }

      Action.ENTER -> {
        roomParticipantService.editRoomParticipantEnterStatus(participantAvro.roomId, participantAvro.id, true)
      }

      Action.EXIT -> {
        roomParticipantService.editRoomParticipantEnterStatus(participantAvro.roomId, participantAvro.id, false)
      }
      
      else -> {
        // No action needed for other actions
      }
    }
  }
}