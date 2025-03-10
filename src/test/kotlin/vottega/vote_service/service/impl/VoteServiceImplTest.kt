package vottega.vote_service.service.impl

import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import vottega.vote_service.adaptor.VoteProducer
import vottega.vote_service.domain.FractionVO
import vottega.vote_service.dto.VoteRequestDTO
import vottega.vote_service.dto.room.ParticipantResponseDTO
import vottega.vote_service.dto.room.ParticipantRoleDTO
import vottega.vote_service.service.VoteService
import vottega.vote_service.service.cache.RoomParticipantService
import java.time.LocalDateTime
import java.util.*


@Transactional
@SpringBootTest
@ActiveProfiles("test")
class VoteServiceImplTest {

  @Autowired
  lateinit var voteService: VoteService

  @MockBean
  lateinit var voteProducer: VoteProducer

  @MockBean
  lateinit var roomParticipantService: RoomParticipantService

  @BeforeEach
  fun setup() {
    // 예시: getRoomParticipantList 메서드가 항상 미리 정의한 목록을 반환하도록 설정
    val sampleParticipant1 = ParticipantResponseDTO(
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

    val sampleParticipant2 = ParticipantResponseDTO(
      id = UUID.randomUUID(),
      name = "테스트 참가자 2",
      roomId = 0,
      position = "테스트 포지션 2",
      participantRole = ParticipantRoleDTO("테스트 1", false),
      isEntered = true,
      createdAt = LocalDateTime.now(),
      enteredAt = LocalDateTime.now(),
      lastUpdatedAt = LocalDateTime.now(),
    )

    val sampleParticipant3 = ParticipantResponseDTO(
      id = UUID.randomUUID(),
      name = "테스트 참가자 3",
      roomId = 0,
      position = "테스트 포지션 3",
      participantRole = ParticipantRoleDTO("테스트 3", true),
      isEntered = false,
      createdAt = LocalDateTime.now(),
      enteredAt = LocalDateTime.now(),
      lastUpdatedAt = LocalDateTime.now(),
    )
    val sampleList = listOf(sampleParticipant1, sampleParticipant2, sampleParticipant3)
    whenever(roomParticipantService.getRoomParticipantList(any())).thenReturn(sampleList)

    whenever(roomParticipantService.getRoomParticipant(any(), any())).thenReturn(sampleParticipant1)
  }


  @Test
  @DisplayName("투표 생성 테스트")
  fun createVote() {
    val newVote = VoteRequestDTO("agendaName", "voteName", null, null, null, null, null, FractionVO(1, 2))
    voteService.createVote(
      0, newVote
    )

    val vote = voteService.getVoteDetail(0)

    assertThat(vote.agendaName).isEqualTo("agendaName")
    assertThat(vote.voteName).isEqualTo("voteName")
    assertThat(vote.passRate.numerator).isEqualTo(1)
    assertThat(vote.passRate.denominator).isEqualTo(2)
  }

  @Test
  fun editVote() {
  }

  @Test
  fun editVoteStatus() {
  }

  @Test
  fun addVotePaper() {
  }

  @Test
  fun getVoteInfo() {
  }

  @Test
  fun getVoteDetail() {
  }

  @Test
  fun resetVote() {
  }
}