package vottega.vote_service.service.cache

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import vottega.vote_service.dto.room.ParticipantResponseDTO
import java.util.*

@Service
class RoomParticipantService(
  private val cacheService: CacheService,
  private val redisTemplate: RedisTemplate<String, Any>
) {
  fun getRoomParticipantList(roomId: Long): List<ParticipantResponseDTO> {
    var participants = redisTemplate.opsForHash<String, ParticipantResponseDTO>()
      .entries("room-participant:$roomId").values.toList()
    if (participants.isEmpty()) {
      participants = cacheService.loadAndCacheRoomInfo(roomId).participants
    }
    return participants
  }

  fun getRoomParticipant(roomId: Long, participantId: UUID): ParticipantResponseDTO? {
    var participant = redisTemplate.opsForHash<String, ParticipantResponseDTO>()
      .get("room-participant:$roomId", participantId.toString())
    if (participant == null) {
      participant = cacheService.loadAndCacheRoomInfo(roomId).participants.find { it.id == participantId }
    }
    return participant
  }

  fun editRoomParticipant(roomId: Long, participant: ParticipantResponseDTO) {
    redisTemplate.opsForHash<String, ParticipantResponseDTO>()
      .put("room-participant:$roomId", participant.id.toString(), participant)
  }

  fun deleteRoomParticipant(roomId: Long, participantId: UUID) {
    redisTemplate.opsForHash<String, ParticipantResponseDTO>()
      .delete("room-participant:$roomId", participantId.toString())
  }

  fun addRoomParticipant(roomId: Long, participant: ParticipantResponseDTO) {
    redisTemplate.opsForHash<String, ParticipantResponseDTO>()
      .put("room-participant:$roomId", participant.id.toString(), participant)
  }
}