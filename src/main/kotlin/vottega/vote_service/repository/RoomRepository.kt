package vottega.vote_service.repository

import jakarta.persistence.Id
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.redis.core.RedisHash
import org.springframework.stereotype.Component
import vottega.vote_service.client.RoomClient
import java.time.LocalDateTime

@Component
class RoomRepository(private val roomClient: RoomClient) {
  @Cacheable(value = ["room-owner"], key = "#roomId")
  fun getRoomOwnerId(roomId: Long): Long {
    return roomClient.getRoom(roomId).ownerId
  }
}