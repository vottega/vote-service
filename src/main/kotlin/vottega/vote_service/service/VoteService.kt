package vottega.vote_service.service

import vottega.vote_service.domain.enum.VotePaperType
import vottega.vote_service.dto.VoteDetailResponseDTO
import vottega.vote_service.dto.VoteResponseDTO
import java.util.*

interface VoteService {
    fun createVote(title: String, roomId: Long, passRateNumerator: Int?, passRateDenominator: Int?)
    fun startVote(voteId: Long)
    fun endVote(voteId: Long)
    fun addVotePaper(voteId: Long, userId: UUID, voteResultType: VotePaperType)
    fun getVoteInfo(roomId: Long) : List<VoteResponseDTO>
    fun getVoteDetail(voteId: Long) : List<VoteDetailResponseDTO>
}