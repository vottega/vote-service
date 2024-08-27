package vottega.vote_service.adaptor.impl

import org.springframework.stereotype.Component
import vottega.vote_service.adaptor.VoteProducer

@Component
class VoteProducerImpl : VoteProducer {

    override fun voteCreatedMessageProduce(voteId: String) {
        TODO("Not yet implemented")
    }

    override fun voteUpdatedMessageProduce(voteId: String) {
        TODO("Not yet implemented")
    }

    override fun votePaperAddedMessageProduce(voteId: String) {
        TODO("Not yet implemented")
    }
}