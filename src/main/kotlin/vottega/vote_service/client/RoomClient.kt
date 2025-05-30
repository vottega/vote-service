package vottega.vote_service.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import vottega.vote_service.config.FeignConfig
import vottega.vote_service.dto.room.RoomResponseDTO

@FeignClient(name = "room-service", configuration = [FeignConfig::class])
interface RoomClient {
  @GetMapping("api/room/{roomId}")
  fun getRoom(@PathVariable("roomId") roomId: Long): RoomResponseDTO
}

