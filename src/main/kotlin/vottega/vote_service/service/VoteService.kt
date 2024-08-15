package vottega.vote_service.service

import vottega.vote_service.domain.enum.VoteResultType
import java.util.*

interface VoteService {
    fun createVote(title : String, roomId: Long)
    fun startVote(voteId: Long)
    fun endVote(voteId: Long)
    fun addVotePaper(voteId: Long, userId: UUID, voteResultType: VoteResultType)
}