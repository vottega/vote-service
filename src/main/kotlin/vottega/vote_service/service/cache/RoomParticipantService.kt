package vottega.vote_service.service.cache

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import vottega.vote_service.dto.room.ParticipantResponseDTO
import java.util.*

@Service
class RoomParticipantService(
  private val cacheService: CacheService,
  private val participantRedisTemplate: RedisTemplate<String, ParticipantResponseDTO>,
  private val participantCacheScriptService: ParticipantCacheScriptService
) {
  fun getRoomParticipantList(roomId: Long): List<ParticipantResponseDTO> {
    var participants = participantRedisTemplate.opsForHash<String, ParticipantResponseDTO>()
      .entries(cacheService.getRoomParticipantCacheKey(roomId)).values.toList()
    if (participants.isEmpty()) {
      participants = cacheService.loadAndCacheRoomInfo(roomId).participants
    }
    return participants
  }

  fun getRoomParticipant(roomId: Long, participantId: UUID): ParticipantResponseDTO? {
    return participantRedisTemplate.opsForHash<String, ParticipantResponseDTO>()
      .get(cacheService.getRoomParticipantCacheKey(roomId), participantId.toString())
  }

  fun isExistRoom(roomId: Long): Boolean =
    participantRedisTemplate.hasKey(cacheService.getRoomParticipantCacheKey(roomId))


  fun editRoomParticipant(roomId: Long, participant: ParticipantResponseDTO) {
    if (isExistRoom(roomId)) {
      participantCacheScriptService.upsertIfNewer(
        cacheService.getRoomParticipantCacheKey(roomId),
        participant,
      )
    } else {
      cacheService.loadAndCacheRoomInfo(roomId)
    }
  }

  fun editRoomParticipantEnterStatus(roomId: Long, participantId: UUID, isEntered: Boolean) {
    val participant = getRoomParticipant(roomId, participantId)
    if (participant != null) {
      val updatedParticipant = participant.copy(isEntered = isEntered)
      editRoomParticipant(roomId, updatedParticipant)
    } else {
      cacheService.loadAndCacheRoomInfo(roomId)
    }
  }

  fun deleteRoomParticipant(roomId: Long, participantId: UUID) {
    participantRedisTemplate.opsForHash<String, ParticipantResponseDTO>()
      .delete(cacheService.getRoomParticipantCacheKey(roomId), participantId.toString())
  }

  fun addRoomParticipant(roomId: Long, participant: ParticipantResponseDTO) {
    if (isExistRoom(roomId)) {
      participantCacheScriptService.upsertIfNewer(
        cacheService.getRoomParticipantCacheKey(roomId),
        participant,
      )
    }
    cacheService.loadAndCacheRoomInfo(roomId)
  }
}