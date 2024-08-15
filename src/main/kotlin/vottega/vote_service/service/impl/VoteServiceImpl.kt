package vottega.vote_service.service.impl

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import vottega.vote_service.domain.Vote
import vottega.vote_service.domain.enum.VoteResultType
import vottega.vote_service.repository.VoteRepository
import vottega.vote_service.service.VoteService
import java.util.*

@Service
@Transactional
class VoteServiceImpl(
    private val voteRepository: VoteRepository
) : VoteService {
    override fun createVote(title: String, roomId: Long) {
        val vote = Vote(title = title, roomId = roomId)
        voteRepository.save(vote)
    }

    override fun startVote(voteId: Long) {
        val vote = voteRepository.findById(voteId).orElseThrow { IllegalArgumentException("Vote not found") }
        vote.startVote()
    }

    override fun endVote(voteId: Long) {
        val vote = voteRepository.findById(voteId).orElseThrow { IllegalArgumentException("Vote not found") }
        vote.endVote()
    }

    override fun addVotePaper(voteId: Long, userId: UUID, voteResultType: VoteResultType) {
        val vote = voteRepository.findById(voteId).orElseThrow { IllegalArgumentException("Vote not found") }
        vote.addVotePaper(userId, voteResultType)
    }
}