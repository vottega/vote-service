package vottega.vote_service.service.cache

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import vottega.vote_service.dto.room.ParticipantResponseDTO
import java.util.*

@Service
class RoomParticipantService(
  private val cacheService: CacheService,
  private val participantRedisTemplate: RedisTemplate<String, ParticipantResponseDTO>,
) {
  fun getRoomParticipantList(roomId: Long): List<ParticipantResponseDTO> {
    var participants = participantRedisTemplate.opsForHash<String, ParticipantResponseDTO>()
      .entries("room-participant:$roomId").values.toList()
    if (participants.isEmpty()) {
      participants = cacheService.loadAndCacheRoomInfo(roomId).participants
    }
    return participants
  }

  fun getRoomParticipant(roomId: Long, participantId: UUID): ParticipantResponseDTO? {
    var participant = participantRedisTemplate.opsForHash<String, ParticipantResponseDTO>()
      .get("room-participant:$roomId", participantId.toString())
    if (participant == null) {
      participant = cacheService.loadAndCacheRoomInfo(roomId).participants.find { it.id == participantId }
    }
    return participant
  }

  fun editRoomParticipant(roomId: Long, participant: ParticipantResponseDTO) {
    if (participantRedisTemplate.hasKey("room-participant:$roomId")) {
      participantRedisTemplate.opsForHash<String, ParticipantResponseDTO>()
        .put("room-participant:$roomId", participant.id.toString(), participant)
    } else {
      cacheService.loadAndCacheRoomInfo(roomId)
    }
  }

  fun deleteRoomParticipant(roomId: Long, participantId: UUID) {
    participantRedisTemplate.opsForHash<String, ParticipantResponseDTO>()
      .delete("room-participant:$roomId", participantId.toString())
  }

  fun addRoomParticipant(roomId: Long, participant: ParticipantResponseDTO) {
    if (participantRedisTemplate.hasKey("room-participant:$roomId")) {
      participantRedisTemplate.opsForHash<String, ParticipantResponseDTO>()
        .put("room-participant:$roomId", participant.id.toString(), participant)
    }
    cacheService.loadAndCacheRoomInfo(roomId)
  }
}