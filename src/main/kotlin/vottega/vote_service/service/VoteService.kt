package vottega.vote_service.service

import jakarta.transaction.Transactional
import org.springframework.security.access.prepost.PreAuthorize
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
import vottega.vote_service.repository.VotePaperRepository
import vottega.vote_service.repository.VoteRepository
import vottega.vote_service.service.cache.RoomParticipantService
import java.util.*

@Service
@Transactional
class VoteService(
  private val voteRepository: VoteRepository,
  private val votePaperRepository: VotePaperRepository,
  private val voteDetailResponseDTOMapper: VoteDetailResponseDTOMapper,
  private val voteResponseDTOMapper: VoteResponseDTOMapper,
  private val voteProducer: VoteProducer,
  private val votePaperMapper: VotePaperMapper,
  private val roomParticipantService: RoomParticipantService
) {
  @PreAuthorize("hasRole('USER') && @voteSecurity.isOwner(#roomId, authentication.principal)")
  fun createVote(roomId: Long, voteRequestDTO: VoteRequestDTO): VoteDetailResponseDTO {
    val vote = Vote(
      agendaName = voteRequestDTO.agendaName,
      voteName = voteRequestDTO.voteName,
      roomId = roomId,
      passRate = voteRequestDTO.passRate,
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


  @PreAuthorize("hasRole('USER') && @voteSecurity.isOwner(#roomId, authentication.principal)")
  fun editVote(roomId: Long, voteRequestDTO: VoteRequestDTO): VoteDetailResponseDTO {
    val vote = voteRepository.findById(roomId).orElseThrow { VoteNotFoundException(roomId) }
    vote.update(
      voteRequestDTO.agendaName,
      voteRequestDTO.voteName,
      voteRequestDTO.passRate,
      voteRequestDTO.isSecret,
      voteRequestDTO.reservedStartTime,
      voteRequestDTO.minParticipantNumber,
      voteRequestDTO.minParticipantRate,
    )
    voteProducer.voteUpdatedMessageProduce(voteResponseDTOMapper.toVoteResponseDTO(vote), VoteAction.EDIT)
    return voteDetailResponseDTOMapper.toVoteDetailResponse(vote)
  }


  @PreAuthorize("hasRole('USER') && @voteSecurity.isOwner(authentication.principal, #voteId)")
  fun editVoteStatusWithSecurity(voteId: Long, action: Status): VoteDetailResponseDTO {
    return editVoteStatus(voteId, action)
  }

  fun editVoteStatus(voteId: Long, action: Status): VoteDetailResponseDTO {
    val vote = voteRepository.findById(voteId).orElseThrow { VoteNotFoundException(voteId) }
    when (action) {
      Status.STARTED -> vote.startVote(roomParticipantService.getRoomParticipantList(vote.roomId))
      Status.ENDED -> vote.endVote()
      else -> throw IllegalArgumentException("Invalid Action")
    }
    votePaperRepository.saveAll(vote.votePaperList) // 왜 이거 해야되는지 확인하자
    voteRepository.save(vote)
    val voteDto = voteResponseDTOMapper.toVoteResponseDTO(vote)
    voteProducer.voteUpdatedMessageProduce(voteDto, VoteAction.STATUS_CHANGE)
    return voteDetailResponseDTOMapper.toVoteDetailResponse(vote)
  }


  @PreAuthorize("@voteSecurity.isParticipantInVote(#voteId, authentication.principal)")
  fun addVotePaper(voteId: Long, userId: UUID, voteResultType: VotePaperType) {
    val vote = voteRepository.findById(voteId).orElseThrow { VoteNotFoundException(voteId) }
    val addedVotePaper = vote.addVotePaper(userId, voteResultType)
    voteProducer.votePaperAddedMessageProduce(votePaperMapper.toVotePaperDTO(addedVotePaper))
  }


  @PreAuthorize("hasRole('USER') && @voteSecurity.isOwner(#roomId, authentication.principal) || hasRole('PARTICIPANT') && @voteSecurity.isParticipantInRoom(#roomId, authentication.principal)")
  fun getVoteInfo(roomId: Long): List<VoteResponseDTO> {
    return voteRepository.findByRoomId(roomId).map {
      voteResponseDTOMapper.toVoteResponseDTO(it)
    }
  }

  @PreAuthorize("hasRole('USER') && @voteSecurity.isOwner(authentication.principal, #voteId) || hasRole('PARTICIPANT') && @voteSecurity.isParticipantInVote(#voteId, authentication.principal)")
  fun getVoteDetail(voteId: Long): VoteDetailResponseDTO {
    val vote = voteRepository.findById(voteId).orElseThrow { VoteNotFoundException(voteId) }
    return voteDetailResponseDTOMapper.toVoteDetailResponse(vote)
  }

  @PreAuthorize("hasRole('USER') && @voteSecurity.isOwner(authentication.principal, #voteId)")
  fun resetVote(voteId: Long) {
    val vote = voteRepository.findById(voteId).orElseThrow { VoteNotFoundException(voteId) }
    vote.resetVote()
    voteProducer.voteUpdatedMessageProduce(voteResponseDTOMapper.toVoteResponseDTO(vote), VoteAction.RESET)
  }
}