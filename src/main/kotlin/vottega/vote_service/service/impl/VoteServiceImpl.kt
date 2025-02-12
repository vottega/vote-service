package vottega.vote_service.service.impl

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import vottega.vote_service.client.RoomClient
import vottega.vote_service.domain.FractionVO
import vottega.vote_service.domain.Vote
import vottega.vote_service.domain.enum.Status
import vottega.vote_service.domain.enum.VotePaperType
import vottega.vote_service.dto.VoteDetailResponseDTO
import vottega.vote_service.dto.VoteRequestDTO
import vottega.vote_service.dto.VoteResponseDTO
import vottega.vote_service.dto.mapper.VoteDetailResponseDTOMapper
import vottega.vote_service.dto.mapper.VoteResponseDTOMapper
import vottega.vote_service.exception.VoteNotFoundException
import vottega.vote_service.repository.VoteRepository
import vottega.vote_service.service.VoteService
import vottega.vote_service.service.cache.RoomOwnerService
import vottega.vote_service.service.cache.RoomParticipantService
import java.util.*

@Service
@Transactional
class VoteServiceImpl(
  private val voteRepository: VoteRepository,
  private val voteDetailResponseDTOMapper: VoteDetailResponseDTOMapper,
  private val voteResponseDTOMapper: VoteResponseDTOMapper,
  private val roomOwnerService: RoomOwnerService,
  private val roomParticipantService: RoomParticipantService,
  private val roomClient: RoomClient
) : VoteService {
  // TODO 방장인지 확인하는 security 로직 추가
  override fun createVote(roomId: Long, voteRequestDTO: VoteRequestDTO): VoteDetailResponseDTO {
    val vote = Vote(
      agendaName = voteRequestDTO.agendaName,
      voteName = voteRequestDTO.voteName,
      roomId = roomId,
      passRate = getFraction(voteRequestDTO.passRateNumerator, voteRequestDTO.passRateDenominator),
      isSecret = voteRequestDTO.isSecret ?: false,
      reservedStartTime = voteRequestDTO.reservedStartTime,
      minParticipantNumber = voteRequestDTO.minParticipantNumber,
      minParticipantRate = voteRequestDTO.minParticipantRate
    )
    val createdVote = voteRepository.save(vote)
    return voteDetailResponseDTOMapper.toVoteDetailResponse(createdVote)
  }


  // TODO 방장인지 확인하는 security 로직 추가
  override fun editVote(roomId: Long, voteRequestDTO: VoteRequestDTO) {
    val vote = voteRepository.findById(roomId).orElseThrow { VoteNotFoundException(roomId) }
    vote.update(
      voteRequestDTO.agendaName,
      voteRequestDTO.voteName,
      getFraction(voteRequestDTO.passRateNumerator, voteRequestDTO.passRateDenominator),
      voteRequestDTO.isSecret,
      voteRequestDTO.reservedStartTime,
      voteRequestDTO.minParticipantNumber,
      voteRequestDTO.minParticipantRate,
    )
  }


  // TODO 방장인지 확인하는 security 로직 추가
  override fun editVoteStatus(voteId: Long, action: Status): VoteDetailResponseDTO { // TODO Enum으로 변경
    val vote = voteRepository.findById(voteId).orElseThrow { VoteNotFoundException(voteId) }
    when (action) {
      Status.STARTED -> vote.startVote(roomClient.getRoom(vote.roomId))
      Status.ENDED -> vote.endVote()
      else -> throw IllegalArgumentException("Invalid Action")
    }
    return voteDetailResponseDTOMapper.toVoteDetailResponse(vote)
  }


  // TODO 방에 있는 사람인지 확인하는 security 로직 추가
  override fun addVotePaper(voteId: Long, userId: UUID, voteResultType: VotePaperType) {
    val vote = voteRepository.findById(voteId).orElseThrow { VoteNotFoundException(voteId) }
    vote.addVotePaper(userId, voteResultType)
  }


  // TODO 방에 있는 사람인지 확인하는 security 로직 추가
  override fun getVoteInfo(roomId: Long): List<VoteResponseDTO> {
    return voteRepository.findByRoomId(roomId).map {
      voteResponseDTOMapper.toVoteResponseDTO(it)
    }
  }

  // TODO 방에 있는 사람인지 확인하는 security 로직 추가
  override fun getVoteDetail(voteId: Long): VoteDetailResponseDTO {
    val vote = voteRepository.findById(voteId).orElseThrow { VoteNotFoundException(voteId) }
    val room = roomClient.getRoom(vote.roomId)
    return voteDetailResponseDTOMapper.toVoteDetailResponse(vote)
  }

  // TODO 방장인지 확인하는 security 로직 추가
  override fun resetVote(voteId: Long) {
    val vote = voteRepository.findById(voteId).orElseThrow { VoteNotFoundException(voteId) }
    vote.resetVote()
  }

  private fun getFraction(numerator: Int?, denominator: Int?): FractionVO? {
    return if (numerator != null && denominator != null) {
      FractionVO(numerator, denominator)
    } else {
      null
    }
  }
}