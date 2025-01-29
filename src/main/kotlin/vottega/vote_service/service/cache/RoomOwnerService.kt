package vottega.vote_service.service.cache

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RoomOwnerService(private val cacheService: CacheService, private val redisTemplate: RedisTemplate<String, Any>) {
  fun getRoomOwner(roomId: Long): Long {
    var roomOwner: Long? = redisTemplate.opsForValue().get("room-owner:$roomId") as Long?
    if (roomOwner == null) {
      roomOwner = cacheService.loadAndCacheRoomInfo(roomId).ownerId
    }
    return roomOwner
  }
}