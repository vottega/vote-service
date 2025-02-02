package vottega.vote_service.adaptor

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import vottega.vote_service.avro.Action
import vottega.vote_service.avro.ParticipantAvro
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
    if (participantAvro.action == Action.ADD) {
      participantMapper.toParticipantResponseDTO(participantAvro).let {
        roomParticipantService.addRoomParticipant(it.roomId, it)
      }
    } else if (participantAvro.action == Action.DELETE) {
      roomParticipantService.deleteRoomParticipant(participantAvro.roomId, participantAvro.id)
    } else {
      participantMapper.toParticipantResponseDTO(participantAvro).let {
        roomParticipantService.editRoomParticipant(it.roomId, it)
      }
    }
  }
}