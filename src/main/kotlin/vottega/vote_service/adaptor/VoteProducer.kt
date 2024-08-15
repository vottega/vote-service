package vottega.vote_service.adaptor

interface VoteProducer {
    fun voteCreatedMessageProduce(voteId: String)
    fun voteUpdatedMessageProduce(voteId: String)
    fun votePaperAddedMessageProduce(voteId: String)

}