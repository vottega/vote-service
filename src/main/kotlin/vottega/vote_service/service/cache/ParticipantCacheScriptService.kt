package vottega.vote_service.service.cache

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Service
import vottega.vote_service.dto.room.ParticipantResponseDTO

@Service
class ParticipantCacheScriptService(
  private val stringRedisTemplate: StringRedisTemplate,
  private val objectMapper: ObjectMapper
) {
  private val upsertScript = DefaultRedisScript(
    """
    local key     = KEYS[1]
    local id      = ARGV[1]
    local jsonNew = ARGV[2]

    local okNew, newObj = pcall(cjson.decode, jsonNew)
    if not okNew or newObj == nil then
      return
    end

    local tsNew = tonumber(newObj["lastUpdatedAtEpochMilli"]) or 0
    local jsonOld = redis.call('HGET', key, id)
    if not jsonOld then
      redis.call('HSET', key, id, jsonNew)
      return
    end

    local okOld, oldObj = pcall(cjson.decode, jsonOld)
    if not okOld or oldObj == nil then
      redis.call('HSET', key, id, jsonNew)
      return
    end

    local tsOld = tonumber(oldObj["lastUpdatedAtEpochMilli"]) or 0
    if tsNew > tsOld then
      redis.call('HSET', key, id, jsonNew)
    end
  """.trimIndent().trimIndent(), Void::class.java
  )

  fun upsertIfNewer(
    key: String,
    participant: ParticipantResponseDTO,
  ) {
    stringRedisTemplate.execute(
      upsertScript,
      listOf(key),
      participant.id.toString(),
      objectMapper.writeValueAsString(participant)
    )
  }
}