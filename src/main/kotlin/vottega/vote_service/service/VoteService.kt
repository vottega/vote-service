package vottega.vote_service.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import vottega.avro.VoteAction
import vottega.vote_service.adaptor.VoteProducer
import vottega.vote_service.domain.Vote
import vottega.vote_service.domain.enum.Status
import vottega.vote_service.domain.enum.VotePaperType
import vottega.vote_service.dto.VoteDetailResponseDTO
import vottega.vote_service.dto.VoteRequestDTO
import vottega.vote_service.dto.VoteResponseDTO
import vottega.vote_service.dto.mapper.VoteDetailResponseDTOMapper
import vottega.vote_service.dto.mapper.VotePaperMapper
import vottega.vote_service.dto.mapper.VoteResponseDTOMapper
import vottega.vote_service.exception.VoteNotFoundException
import vottega.vote_service.repository.VoteRepository
import vottega.vote_service.service.cache.RoomParticipantService
import java.util.*

@Service
@Transactional
class VoteService(
  private val voteRepository: VoteRepository,
  private val voteDetailResponseDTOMapper: VoteDetailResponseDTOMapper,
  private val voteResponseDTOMapper: VoteResponseDTOMapper,
  private val voteProducer: VoteProducer,
  private val votePaperMapper: VotePaperMapper,
  private val roomParticipantService: RoomParticipantService
) {
  // TODO 방장인지 확인하는 security 로직 추가
  fun createVote(roomId: Long, voteRequestDTO: VoteRequestDTO): VoteDetailResponseDTO {
    val vote = Vote(
      agendaName = voteRequestDTO.agendaName,
      voteName = voteRequestDTO.voteName,
      roomId = roomId,
      passRate = voteRequestDTO.minPassRate,
      isSecret = voteRequestDTO.isSecret ?: false,
      reservedStartTime = voteRequestDTO.reservedStartTime,
      minParticipantNumber = voteRequestDTO.minParticipantNumber,
      minParticipantRate = voteRequestDTO.minParticipantRate
    )
    val createdVote = voteRepository.save(vote)
    voteProducer.voteUpdatedMessageProduce(
      voteResponseDTOMapper.toVoteResponseDTO(createdVote),
      VoteAction.STATUS_CHANGE
    )
    return voteDetailResponseDTOMapper.toVoteDetailResponse(createdVote)
  }


  // TODO 방장인지 확인하는 security 로직 추가
  fun editVote(roomId: Long, voteRequestDTO: VoteRequestDTO): VoteDetailResponseDTO {
    val vote = voteRepository.findById(roomId).orElseThrow { VoteNotFoundException(roomId) }
    vote.update(
      voteRequestDTO.agendaName,
      voteRequestDTO.voteName,
      voteRequestDTO.minPassRate,
      voteRequestDTO.isSecret,
      voteRequestDTO.reservedStartTime,
      voteRequestDTO.minParticipantNumber,
      voteRequestDTO.minParticipantRate,
    )
    voteProducer.voteUpdatedMessageProduce(voteResponseDTOMapper.toVoteResponseDTO(vote), VoteAction.EDIT)
    return voteDetailResponseDTOMapper.toVoteDetailResponse(vote)
  }


  // TODO 방장인지 확인하는 security 로직 추가
  fun editVoteStatus(voteId: Long, action: Status): VoteDetailResponseDTO { // TODO Enum으로 변경
    val vote = voteRepository.findById(voteId).orElseThrow { VoteNotFoundException(voteId) }
    when (action) {
      Status.STARTED -> vote.startVote(roomParticipantService.getRoomParticipantList(vote.roomId))
      Status.ENDED -> vote.endVote()
      else -> throw IllegalArgumentException("Invalid Action")
    }
    voteRepository.save(vote)
    val voteDto = voteResponseDTOMapper.toVoteResponseDTO(vote)
    voteProducer.voteUpdatedMessageProduce(voteDto, VoteAction.STATUS_CHANGE)
    return voteDetailResponseDTOMapper.toVoteDetailResponse(vote)
  }


  // TODO 방에 있는 사람인지 확인하는 security 로직 추가
  fun addVotePaper(voteId: Long, userId: UUID, voteResultType: VotePaperType) {
    val vote = voteRepository.findById(voteId).orElseThrow { VoteNotFoundException(voteId) }
    val addedVotePaper = vote.addVotePaper(userId, voteResultType)
    voteProducer.votePaperAddedMessageProduce(votePaperMapper.toVotePaperDTO(addedVotePaper))
  }


  // TODO 방에 있는 사람인지 확인하는 security 로직 추가
  fun getVoteInfo(roomId: Long): List<VoteResponseDTO> {
    return voteRepository.findByRoomId(roomId).map {
      voteResponseDTOMapper.toVoteResponseDTO(it)
    }
  }

  // TODO 방에 있는 사람인지 확인하는 security 로직 추가
  fun getVoteDetail(voteId: Long): VoteDetailResponseDTO {
    val vote = voteRepository.findById(voteId).orElseThrow { VoteNotFoundException(voteId) }
    return voteDetailResponseDTOMapper.toVoteDetailResponse(vote)
  }

  // TODO 방장인지 확인하는 security 로직 추가
  fun resetVote(voteId: Long) {
    val vote = voteRepository.findById(voteId).orElseThrow { VoteNotFoundException(voteId) }
    vote.resetVote()
    voteProducer.voteUpdatedMessageProduce(voteResponseDTOMapper.toVoteResponseDTO(vote), VoteAction.RESET)
  }
}