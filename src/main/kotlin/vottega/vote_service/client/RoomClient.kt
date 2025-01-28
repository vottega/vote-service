package vottega.vote_service.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import vottega.vote_service.config.FeignConfig
import vottega.vote_service.dto.room.RoomResponseDTO

@FeignClient(name = "roomService", url = "\${auth.service.url}", configuration = [FeignConfig::class])
interface RoomClient {
  @GetMapping("api/room/{roomId}")
  fun getRoom(roomId: Long): RoomResponseDTO
}

