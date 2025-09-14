package vottega.vote_service.service.cache

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import vottega.vote_service.client.RoomClient
import vottega.vote_service.dto.room.ParticipantResponseDTO
import vottega.vote_service.dto.room.RoomResponseDTO

@Service
class CacheService(
  private val redisClient: RoomClient,
  private val participantRedisTemplate: RedisTemplate<String, ParticipantResponseDTO>,
  private val longRedisTemplate: RedisTemplate<String, Long>
) {

  fun loadAndCacheRoomInfo(roomId: Long): RoomResponseDTO {
    val room = redisClient.getRoom(roomId)

    longRedisTemplate.opsForValue().set("room-owner:$roomId", room.ownerId)
    participantRedisTemplate.opsForHash<String, ParticipantResponseDTO>()
      .putAll(
        "room-participant:$roomId",
        room.participants.associateBy({ it.id.toString() }, { it })
      )

    return room
  }
}