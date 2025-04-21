package vottega.vote_service.service.impl

import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import vottega.vote_service.adaptor.VoteProducer
import vottega.vote_service.domain.FractionVO
import vottega.vote_service.domain.enum.Status
import vottega.vote_service.domain.enum.VotePaperType
import vottega.vote_service.domain.enum.VoteResult
import vottega.vote_service.dto.VoteRequestDTO
import vottega.vote_service.dto.room.ParticipantResponseDTO
import vottega.vote_service.dto.room.ParticipantRoleDTO
import vottega.vote_service.exception.VoteForbiddenException
import vottega.vote_service.exception.VoteStatusConflictException
import vottega.vote_service.service.VoteScheduler
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

  @Autowired
  lateinit var voteScheduler: VoteScheduler

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

    whenever(roomParticipantService.getRoomParticipant(any(), eq(sampleParticipant1.id))).thenReturn(sampleParticipant1)
    whenever(roomParticipantService.getRoomParticipant(any(), eq(sampleParticipant2.id))).thenReturn(sampleParticipant2)
    whenever(roomParticipantService.getRoomParticipant(any(), eq(sampleParticipant3.id))).thenReturn(sampleParticipant3)
  }


  @Test
  fun `투표는 DTO와 똑같은 정보로 생성이 되어야 한다`() {
    val newVote = VoteRequestDTO("agendaName", "voteName", FractionVO(1, 3), null, null, null, FractionVO(1, 2))
    val createdVote = voteService.createVote(
      0, newVote
    )

    val vote = voteService.getVoteDetail(createdVote.id)

    assertThat(vote.agendaName).isEqualTo("agendaName")
    assertThat(vote.voteName).isEqualTo("voteName")
    assertThat(vote.passRate.numerator).isEqualTo(1)
    assertThat(vote.passRate.denominator).isEqualTo(3)
    assertThat(vote.minParticipantRate.numerator).isEqualTo(1)
    assertThat(vote.minParticipantRate.denominator).isEqualTo(2)
  }

  @Test
  fun `투표는 수정 DTO와 똑같은 정보로 수정이 되어야 한다`() {
    val newVote = VoteRequestDTO("agendaName", "voteName", FractionVO(1, 2), null, null, null, FractionVO(1, 2))
    val createdVote = voteService.createVote(
      0, newVote
    )

    val updateVote = VoteRequestDTO("newAgenda", "newVote", FractionVO(1, 3), true, null, 3, FractionVO(1, 3))
    val editedVote = voteService.editVote(createdVote.id, updateVote)

    assertThat(editedVote.agendaName).isEqualTo("newAgenda")
    assertThat(editedVote.voteName).isEqualTo("newVote")
    assertThat(editedVote.passRate.numerator).isEqualTo(1)
    assertThat(editedVote.passRate.denominator).isEqualTo(3)
    assertThat(editedVote.isSecret).isTrue()
    assertThat(editedVote.minParticipantNumber).isEqualTo(3)
    assertThat(editedVote.minParticipantRate.numerator).isEqualTo(1)
    assertThat(editedVote.minParticipantRate.denominator).isEqualTo(3)
  }

  @Test
  fun `투표는 CREATED일 때에 시작이될 수 있고 시작 시에 투표가 가능한 참가자들의 NOT_VOTED 투표용지가 있고 상태는 NOT_DECIDED여야 한다`() {
    val newVote = VoteRequestDTO("agendaName", "voteName", FractionVO(1, 2), null, null, null, FractionVO(1, 2))
    val createdVote = voteService.createVote(
      0, newVote
    )

    val startedVote = voteService.editVoteStatus(createdVote.id, Status.STARTED)

    //started로 변경되어야함
    assertThat(startedVote.status).isEqualTo(Status.STARTED)
    //투표용지가 3개여야됨 Mock에 투표 가능한 참가자 2명으로 설정해 놓았으므로
    assertThat(startedVote.votePaperList.size).isEqualTo(1)
    // 투표가 모두 빈종이여야됨
    assertThat(startedVote.votePaperList).allSatisfy { it.votePaperType === VotePaperType.NOT_VOTED }
    // 투표는 아직 결정되지 않았어야함
    assertThat(startedVote.result).isEqualTo(VoteResult.NOT_DECIDED)
  }

  @Test
  fun `투표 시작 시에 최소 참가자 비율을 만족시키지 못하면 에러를 내야 한다`() {
    val newVote = VoteRequestDTO("agendaName", "voteName", FractionVO(1, 2), null, null, null, FractionVO(1, 1))
    val createdVote = voteService.createVote(
      0, newVote
    )

    assertThrows<VoteStatusConflictException> { voteService.editVoteStatus(createdVote.id, Status.STARTED) }
  }

  @Test
  fun `투표 시작 시에 최소 참가자 수를 만족시키지 못하면 에러를 내야 한다`() {
    val newVote = VoteRequestDTO("agendaName", "voteName", FractionVO(1, 2), null, null, 3, FractionVO(1, 2))
    val createdVote = voteService.createVote(
      0, newVote
    )

    assertThrows<VoteStatusConflictException> { voteService.editVoteStatus(createdVote.id, Status.STARTED) }
  }

  @Test
  fun `CREATED일 경우 STARTED로의 상태 변경만 가능하다`() {
    val newVote = VoteRequestDTO("agendaName", "voteName", FractionVO(1, 2), null, null, null, FractionVO(1, 2))
    val createdVote = voteService.createVote(
      0, newVote
    )
    assertThrows<VoteStatusConflictException> { voteService.editVoteStatus(createdVote.id, Status.ENDED) }

    assertThrows<IllegalArgumentException> { voteService.editVoteStatus(createdVote.id, Status.CREATED) }
  }

  @Test
  fun `투표를 할 경우 투표한 대로 저장이 되어야 한다`() {
    val newVote = VoteRequestDTO("agendaName", "voteName", FractionVO(1, 2), null, null, null, FractionVO(1, 2))
    val createdVote = voteService.createVote(
      0, newVote
    )

    val startedVote = voteService.editVoteStatus(createdVote.id, Status.STARTED)

    voteService.addVotePaper(createdVote.id, startedVote.votePaperList[0].userId, VotePaperType.YES)
    assertThat(voteService.getVoteDetail(createdVote.id).votePaperList.size).isEqualTo(1)
    assertThat(voteService.getVoteDetail(createdVote.id).votePaperList[0].votePaperType).isEqualTo(VotePaperType.YES)
  }

  @Test
  fun `투표를 하려면 투표권이 있거나 참가를 하고 있어야 한다`() {
    val newVote = VoteRequestDTO("agendaName", "voteName", FractionVO(1, 2), null, null, null, FractionVO(1, 2))
    val createdVote = voteService.createVote(
      0, newVote
    )

    val startedVote = voteService.editVoteStatus(createdVote.id, Status.STARTED)

    val participantList = roomParticipantService.getRoomParticipantList(startedVote.roomId)


    // 투표권이 없으므로
    assertThrows<VoteForbiddenException> {
      voteService.addVotePaper(createdVote.id, participantList[1].id, VotePaperType.YES)
    }

    // 시작할 때에 방에 들어와있지 않았으므로
    assertThrows<VoteForbiddenException> {
      voteService.addVotePaper(createdVote.id, participantList[2].id, VotePaperType.YES)
    }
  }

  @Test
  fun `투표 종료 시에 passRate 이상이라면 투표 결과가 PASSED 여야 한다`() {
    val newVote = VoteRequestDTO("agendaName", "voteName", FractionVO(1, 2), null, null, null, FractionVO(1, 2))
    val createdVote = voteService.createVote(
      0, newVote
    )

    val startedVote = voteService.editVoteStatus(createdVote.id, Status.STARTED)

    voteService.addVotePaper(createdVote.id, startedVote.votePaperList[0].userId, VotePaperType.YES)
    val endedVote = voteService.editVoteStatus(createdVote.id, Status.ENDED)
    assertThat(endedVote.status).isEqualTo(Status.ENDED)
    assertThat(endedVote.result).isEqualTo(VoteResult.PASSED)
  }

  @Test
  fun `투표 종료 시에 passRate 미만이라면 투표 결과가 REJECTED가 나와야 한다`() {
    val newVote = VoteRequestDTO("agendaName", "voteName", FractionVO(1, 2), null, null, null, FractionVO(1, 2))
    val createdVote = voteService.createVote(
      0, newVote
    )

    val startedVote = voteService.editVoteStatus(createdVote.id, Status.STARTED)

    voteService.addVotePaper(createdVote.id, startedVote.votePaperList[0].userId, VotePaperType.NO)
    val endedVote = voteService.editVoteStatus(createdVote.id, Status.ENDED)
    assertThat(endedVote.status).isEqualTo(Status.ENDED)
    assertThat(endedVote.result).isEqualTo(VoteResult.REJECTED)
  }

  @Test
  fun `투표 초기화 시에 상태는 CREATED로 변경이 되고 votePaper의 개수는 0개여야 한다`() {
    val newVote = VoteRequestDTO("agendaName", "voteName", FractionVO(1, 2), null, null, null, FractionVO(1, 2))
    val createdVote = voteService.createVote(
      0, newVote
    )

    val startedVote = voteService.editVoteStatus(createdVote.id, Status.STARTED)

    voteService.addVotePaper(createdVote.id, startedVote.votePaperList[0].userId, VotePaperType.YES)
    voteService.resetVote(createdVote.id)
    val resetVote = voteService.getVoteDetail(createdVote.id)
    assertThat(resetVote.status).isEqualTo(Status.CREATED)
    assertThat(resetVote.votePaperList.size).isEqualTo(0)
  }

  @Test
  fun `투표 예약 시에 투표 예약 시간이 될 경우 자동으로 투표가 시작되어야 한다`() {
    val newVote = VoteRequestDTO(
      "agendaName",
      "voteName",
      FractionVO(1, 2),
      null,
      LocalDateTime.now(),
      null,
      FractionVO(1, 2)
    )
    val createdVote = voteService.createVote(
      0, newVote
    )
    voteScheduler.checkReservedVote()
    val startedVote = voteService.getVoteDetail(createdVote.id)
    assertThat(startedVote.status).isEqualTo(Status.STARTED)
  }
}