package vottega.vote_service.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import vottega.room_service.dto.RoomResponseDTO

@FeignClient
interface RoomClient {
    @GetMapping("api/room/{roomId}")
    fun getRoom(roomId: Long) : RoomResponseDTO
}

