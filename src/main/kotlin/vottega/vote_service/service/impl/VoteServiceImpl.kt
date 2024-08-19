package vottega.vote_service.service.impl

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import vottega.vote_service.client.RoomClient
import vottega.vote_service.domain.FractionVO
import vottega.vote_service.domain.Vote
import vottega.vote_service.domain.enum.VotePaperType
import vottega.vote_service.dto.VoteDetailResponseDTO
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
    override fun createVote(title: String, roomId: Long, passRateNumerator: Int?, passRateDenominator: Int?) {
        val room = roomClient.getRoom(roomId) //TODO 404 에러같은 예외 처리
        val fraction = if(passRateNumerator != null && passRateDenominator != null) {
            FractionVO(passRateNumerator, passRateDenominator)
        } else {
            FractionVO(1, 2)
        }
        val vote = Vote(
            title = title,
            roomId = roomId,
            fraction
        )
        voteRepository.save(vote)
    }

    override fun startVote(voteId: Long) {
        val vote = voteRepository.findById(voteId).orElseThrow { IllegalArgumentException("Vote not found") }

        val room = roomClient.getRoom(vote.roomId)
        vote.startVote(room.participants.filter { it.canVote && it.isEntered }.map { it.id }.toMutableList())
    }

    override fun endVote(voteId: Long) {
        val vote = voteRepository.findById(voteId).orElseThrow { IllegalArgumentException("Vote not found") }
        vote.endVote()
    }

    override fun addVotePaper(voteId: Long, userId: UUID, voteResultType: VotePaperType) {
        val vote = voteRepository.findById(voteId).orElseThrow { IllegalArgumentException("Vote not found") }
        vote.addVotePaper(userId, voteResultType)
    }

    override fun getVoteInfo(roomId: Long): List<VoteResponseDTO> {
        return voteRepository.findByRoomId(roomId).map {
            VoteResponseDTO(
                title = it.title,
                status = it.status,
                createdAt = it.createdAt,
                yesNum = it.votePaperList.count { it.voteResultType == VotePaperType.YES },
                noNum = it.votePaperList.count { it.voteResultType == VotePaperType.NO },
                abstainNum = it.votePaperList.count { it.voteResultType == VotePaperType.ABSTAIN },
                result = it.voteResultType
            )
        }
    }

    override fun getVoteDetail(voteId: Long): List<VoteDetailResponseDTO> {
        TODO("Not yet implemented")
    }
}