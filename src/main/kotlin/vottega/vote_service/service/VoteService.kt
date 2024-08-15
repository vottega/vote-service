package vottega.vote_service.service

interface VoteService {
    fun createVote(title : String, roomId: Long)
    fun startVote(voteId: Long)
    fun endVote(voteId: Long)
    fun vote(voteId: Long, userId: Long, voteResultType: String)
}