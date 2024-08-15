package vottega.vote_service.service.impl

import org.springframework.stereotype.Service
import vottega.vote_service.service.VoteService

@Service
class VoteServiceImpl : VoteService {
    override fun createVote(title: String, roomId: Long) {
        TODO("Not yet implemented")
    }

    override fun startVote(voteId: Long) {
        TODO("Not yet implemented")
    }

    override fun endVote(voteId: Long) {
        TODO("Not yet implemented")
    }

    override fun vote(voteId: Long, userId: Long, voteResultType: String) {
        TODO("Not yet implemented")
    }
}