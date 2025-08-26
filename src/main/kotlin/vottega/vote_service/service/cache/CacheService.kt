package vottega.vote_service.service.cache

import org.redisson.api.RedissonClient
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Service
import vottega.vote_service.client.RoomClient
import vottega.vote_service.dto.room.ParticipantResponseDTO
import vottega.vote_service.dto.room.RoomResponseDTO
import java.util.concurrent.TimeUnit

@Service
class CacheService(
  private val redisClient: RoomClient,
  private val participantRedisTemplate: RedisTemplate<String, ParticipantResponseDTO>,
  private val redisson: RedissonClient,
  private val longRedisTemplate: RedisTemplate<String, Long>,
  private val participantCacheScriptService: ParticipantCacheScriptService,
) {
  private val upsertScript = DefaultRedisScript(
    """
    local participantsKey = KEYS[1]
    local updatedAtKey    = KEYS[2]

    local updated = {}
    local skipped = {}

    for i = 1, #ARGV, 3 do
      local pid   = ARGV[i]
      local ts    = tonumber(ARGV[i + 1])
      local json  = ARGV[i + 2]

      local cur = tonumber(redis.call('HGET', updatedAtKey, pid) or '-1')

      if (cur == nil) or (ts > cur) then
        redis.call('HSET', participantsKey, pid, json)
        redis.call('HSET', updatedAtKey, pid, ts)
        table.insert(updated, pid)
      else
        table.insert(skipped, pid)
      end
    end

    return cjson.encode({ updated = updated, skipped = skipped })
  """.trimIndent(), String::class.java
  )


  fun loadAndCacheRoomInfo(roomId: Long): RoomResponseDTO {
    val bucket = redisson.getBucket<RoomResponseDTO>(getBucketKey(roomId))
    val latch = redisson.getCountDownLatch(getRoomLoadLatchKey(roomId))
    val iAmLeader = latch.trySetCount(1)
    return if (iAmLeader) {
      try {
        val room = redisClient.getRoom(roomId)
        longRedisTemplate.opsForValue().set(getRoomOwnerCacheKey(roomId), room.ownerId)
        val key = getRoomParticipantCacheKey(roomId)
        room.participants.forEach {
          participantCacheScriptService.upsertIfNewer(
            key,
            it,
          )
        }
        room.participants = participantRedisTemplate.opsForHash<String, ParticipantResponseDTO>()
          .entries(getRoomParticipantCacheKey(roomId)).values.toList()
        bucket.set(room)
        room
      } finally {
        latch.countDown()
        latch.delete()
      }
    } else {
      val ok = latch.await(10, TimeUnit.SECONDS)
      val cached = bucket.get()
      if (!ok || cached == null) {
        throw RuntimeException("룸 정보를 불러오는데 실패했습니다.")
      }
      cached
    }
  }

  fun getRoomOwnerCacheKey(roomId: Long) = "room:{$roomId}:owner"
  fun getRoomLoadLatchKey(roomId: Long) = "room:{$roomId}:latch"
  fun getBucketKey(roomId: Long) = "room:{$roomId}"
  fun getRoomParticipantCacheKey(roomId: Long) = "room:{$roomId}:participant"
}