package vottega.vote_service.service.impl

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import vottega.vote_service.client.RoomClient
import vottega.vote_service.domain.FractionVO
import vottega.vote_service.domain.Vote
import vottega.vote_service.domain.enum.VotePaperType
import vottega.vote_service.dto.VoteDetailResponseDTO
import vottega.vote_service.dto.VoteRequestDTO
import vottega.vote_service.dto.VoteResponseDTO
import vottega.vote_service.dto.mapper.VoteDetailResponseDTOMapper
import vottega.vote_service.dto.mapper.VoteResponseDTOMapper
import vottega.vote_service.exception.VoteNotFoundException
import vottega.vote_service.repository.VoteRepository
import vottega.vote_service.service.VoteService
import java.util.*

@Service
@Transactional
class VoteServiceImpl(
    private val voteRepository: VoteRepository,
    private val roomClient: RoomClient,
    private val voteDetailResponseDTOMapper: VoteDetailResponseDTOMapper,
    private val voteResponseDTOMapper: VoteResponseDTOMapper,
) : VoteService {
    override fun createVote(roomId: Long, voteRequestDTO: VoteRequestDTO): VoteDetailResponseDTO {
        val room = roomClient.getRoom(roomId) //TODO 404 에러같은 예외 처리
        val fraction = if (voteRequestDTO.passRateNumerator != null && voteRequestDTO.passRateDenominator != null) {
            FractionVO(voteRequestDTO.passRateNumerator, voteRequestDTO.passRateDenominator)
        } else {
            FractionVO(1, 2)
        }
        val vote = Vote(
            title = voteRequestDTO.title,
            roomId = roomId,
            fraction
        )
        val createdVote = voteRepository.save(vote)
        return voteDetailResponseDTOMapper.toVoteDetailResponse(createdVote, room)
    }

    override fun editVoteStatus(voteId: Long, action: String): VoteDetailResponseDTO {
        val vote = voteRepository.findById(voteId).orElseThrow { VoteNotFoundException(voteId) }
        val room = roomClient.getRoom(vote.roomId)
        when (action) {
            "start" -> vote.startVote(room)
            "end" -> vote.endVote()
            else -> throw IllegalArgumentException("Invalid Action")
        }
        return voteDetailResponseDTOMapper.toVoteDetailResponse(vote, room)
    }


    override fun addVotePaper(voteId: Long, userId: UUID, voteResultType: VotePaperType) {
        val vote = voteRepository.findById(voteId).orElseThrow { VoteNotFoundException(voteId) }
        vote.addVotePaper(userId, voteResultType)
    }

    override fun getVoteInfo(roomId: Long): List<VoteResponseDTO> {
        return voteRepository.findByRoomId(roomId).map {
            voteResponseDTOMapper.mapToResponse(it)
        }
    }

    override fun getVoteDetail(voteId: Long): VoteDetailResponseDTO {
        val vote = voteRepository.findById(voteId).orElseThrow { VoteNotFoundException(voteId) }
        val room = roomClient.getRoom(vote.roomId)
        return voteDetailResponseDTOMapper.toVoteDetailResponse(vote, room)
    }

    override fun resetVote(voteId: Long) {
        val vote = voteRepository.findById(voteId).orElseThrow { VoteNotFoundException(voteId) }
        vote.resetVote()
    }
}