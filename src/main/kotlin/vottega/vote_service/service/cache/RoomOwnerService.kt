package vottega.vote_service.service.cache

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RoomOwnerService(
  private val cacheService: CacheService,
  private val longRedisTemplate: RedisTemplate<String, Long>
) {
  fun getRoomOwner(roomId: Long): Long {
    var roomOwner: Long? = longRedisTemplate.opsForValue().get("room-owner:$roomId")
    if (roomOwner == null) {
      roomOwner = cacheService.loadAndCacheRoomInfo(roomId).ownerId
    }
    return roomOwner
  }
}