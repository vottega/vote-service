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
import vottega.vote_service.repository.VoteRepository
import vottega.vote_service.service.VoteService
import java.util.*

@Service
@Transactional
class VoteServiceImpl(
    private val voteRepository: VoteRepository,
    private val roomClient: RoomClient
) : VoteService {
    override fun createVote(roomId: Long, voteRequestDTO: VoteRequestDTO) : VoteDetailResponseDTO {
        val room = roomClient.getRoom(roomId) //TODO 404 에러같은 예외 처리
        val fraction = if(voteRequestDTO.passRateNumerator != null && voteRequestDTO.passRateDenominator != null) {
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
    }

    override fun editVoteStatus(voteId: Long, action : String) : VoteDetailResponseDTO {
        val vote = voteRepository.findById(voteId).orElseThrow { IllegalArgumentException("Vote not found") }
        when (action) {
            "start" -> startVote(vote)
            "end" -> vote.endVote()
        }

    }

    private fun startVote(vote: Vote) {
        val room = roomClient.getRoom(vote.roomId)
        vote.startVote(room)
    }


    override fun addVotePaper(voteId: Long, userId: UUID, voteResultType: VotePaperType) {
        val vote = voteRepository.findById(voteId).orElseThrow { IllegalArgumentException("Vote not found") }
        vote.addVotePaper(userId, voteResultType)
    }

    override fun getVoteInfo(roomId: Long): List<VoteResponseDTO> {
        return voteRepository.findByRoomId(roomId).map {
            VoteResponseDTO(
                id = it.id,
                title = it.title,
                status = it.status,
                createdAt = it.createdAt,
                yesNum = it.votePaperList.count { it.voteResultType == VotePaperType.YES },
                noNum = it.votePaperList.count { it.voteResultType == VotePaperType.NO },
                abstainNum = it.votePaperList.count { it.voteResultType == VotePaperType.ABSTAIN },
                result = it.result
            )
        }
    }

    override fun getVoteDetail(voteId: Long): List<VoteDetailResponseDTO> {
        TODO("Not yet implemented")
    }
}