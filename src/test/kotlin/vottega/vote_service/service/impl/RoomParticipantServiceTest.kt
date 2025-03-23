package vottega.vote_service.service.impl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import vottega.vote_service.client.RoomClient
import vottega.vote_service.dto.room.ParticipantResponseDTO
import vottega.vote_service.dto.room.ParticipantRoleDTO
import vottega.vote_service.dto.room.RoomResponseDTO
import vottega.vote_service.dto.room.RoomStatus
import vottega.vote_service.service.cache.CacheService
import vottega.vote_service.service.cache.RoomParticipantService
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test

@Testcontainers
@SpringBootTest(
  properties = [
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
  ]
)
@ActiveProfiles("test")
class RoomParticipantServiceTest {
  @Autowired
  private lateinit var roomParticipantService: RoomParticipantService

  @Autowired
  private lateinit var cacheService: CacheService

  @MockBean
  private lateinit var roomClient: RoomClient

  companion object {
    // Redis 컨테이너 생성. 필요한 경우 이미지 버전이나 옵션을 수정하세요.
    @Container
    val redisContainer = GenericContainer<Nothing>("redis:latest").apply {
      withExposedPorts(6379)
    }

    @JvmStatic
    @DynamicPropertySource
    fun setRedisProperties(registry: DynamicPropertyRegistry) {
      registry.add("spring.data.redis.host") { redisContainer.host }
      registry.add("spring.data.redis.port") { redisContainer.getMappedPort(6379) }
    }
  }


  private final val sampleParticipant1 = ParticipantResponseDTO(
    id = UUID.randomUUID(),
    name = "테스트 참가자 1",
    roomId = 0,
    position = "테스트 포지션 1",
    participantRole = ParticipantRoleDTO("테스트", true),
    isEntered = true,
    createdAt = LocalDateTime.now(),
    enteredAt = LocalDateTime.now(),
    lastUpdatedAt = LocalDateTime.now(),
  )

  private final val sampleParticipant2 = ParticipantResponseDTO(
    id = UUID.randomUUID(),
    name = "테스트 참가자 2",
    roomId = 0,
    position = "테스트 포지션 2",
    participantRole = ParticipantRoleDTO("테스트", false),
    isEntered = true,
    createdAt = LocalDateTime.now(),
    enteredAt = LocalDateTime.now(),
    lastUpdatedAt = LocalDateTime.now(),
  )

  private final val sampleParticipant3 = ParticipantResponseDTO(
    id = UUID.randomUUID(),
    name = "테스트 참가자 3",
    roomId = 0,
    position = "테스트 포지션 3",
    participantRole = ParticipantRoleDTO("테스트", true),
    isEntered = false,
    createdAt = LocalDateTime.now(),
    enteredAt = LocalDateTime.now(),
    lastUpdatedAt = LocalDateTime.now(),
  )

  val sampleRoom = RoomResponseDTO(
    id = 0,
    name = "테스트 방",
    ownerId = 0,
    status = RoomStatus.PROGRESS,
    participants = listOf(sampleParticipant1, sampleParticipant2, sampleParticipant3),
    roles = listOf(ParticipantRoleDTO("테스트", true)),
    createdAt = LocalDateTime.now(),
    lastUpdatedAt = LocalDateTime.now(),
    startedAt = LocalDateTime.now(),
    finishedAt = null,
  )

  @BeforeEach
  fun setup() {
    whenever(roomClient.getRoom(any())).thenReturn(sampleRoom)
  }

  @Test
  @DisplayName("방 참가자 목록 조회 테스트")
  fun getRoomParticipantListTest() {
    val roomParticipantList = roomParticipantService.getRoomParticipantList(sampleRoom.id)
    assertThat(roomParticipantList.size).isEqualTo(3)
  }

  @Test
  @DisplayName("방 참가자 조회 테스트")
  fun getRoomParticipantTest() {
    val roomParticipantList = roomParticipantService.getRoomParticipant(sampleRoom.id, sampleParticipant1.id)
    assertThat(roomParticipantList).isEqualTo(sampleParticipant1)
  }

  @Test
  @DisplayName("방 참가자 업데이트 테스트")
  fun updateRoomParticipantTest() {
    roomParticipantService.getRoomParticipantList(sampleRoom.id) // 캐싱해두기
    val updatedParticipant = sampleParticipant1.copy(name = "테스트 참가자 1 업데이트", isEntered = false)
    roomParticipantService.editRoomParticipant(sampleRoom.id, updatedParticipant)
    val participant = roomParticipantService.getRoomParticipant(sampleRoom.id, updatedParticipant.id)
    assertThat(participant).isEqualTo(updatedParticipant)
  }

  @Test
  @DisplayName("방 참가자 삭제 테스트")
  fun deleteRoomParticipantTest() {
    roomParticipantService.getRoomParticipantList(sampleRoom.id) // 캐싱해두기
    roomParticipantService.deleteRoomParticipant(sampleRoom.id, sampleParticipant1.id)
    val participant = roomParticipantService.getRoomParticipantList(sampleRoom.id)
      .find { it.id == sampleParticipant1.id }
    assertThat(participant).isNull()
  }

}