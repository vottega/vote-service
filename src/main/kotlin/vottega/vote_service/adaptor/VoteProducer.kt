package vottega.vote_service.adaptor

import vottega.vote_service.avro.VoteAction
import vottega.vote_service.dto.VotePaperDTO
import vottega.vote_service.dto.VoteResponseDTO

interface VoteProducer {
  fun voteUpdatedMessageProduce(voteResponseDTO: VoteResponseDTO, action: VoteAction)
  fun votePaperAddedMessageProduce(votePaperDTO: VotePaperDTO)

}