package vottega.vote_service.adaptor.impl

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import vottega.vote_service.adaptor.VoteProducer
import vottega.vote_service.avro.VoteAction
import vottega.vote_service.avro.VoteAvro
import vottega.vote_service.avro.VotePaperAvro
import vottega.vote_service.dto.VotePaperDTO
import vottega.vote_service.dto.VoteResponseDTO
import vottega.vote_service.dto.mapper.VotePaperMapper
import vottega.vote_service.dto.mapper.VoteResponseDTOMapper

@Component
class VoteProducerImpl(
  private val voteKafkaTemplate: KafkaTemplate<Long, VoteAvro>,
  private val votePaperKafkaTemplate: KafkaTemplate<Long, VotePaperAvro>,
  private val voteResponseDTOMapper: VoteResponseDTOMapper,
  private val votePaperMapper: VotePaperMapper,
) : VoteProducer {
  override fun voteUpdatedMessageProduce(voteResponseDTO: VoteResponseDTO, action: VoteAction) {
    voteResponseDTOMapper.toVoteAvro(voteResponseDTO, action).let {
      voteKafkaTemplate.send("vote", it.roomId, it)
    }
  }


  override fun votePaperAddedMessageProduce(votePaperDTO: VotePaperDTO) {
    votePaperMapper.toVotePaperAvro(votePaperDTO).let {
      votePaperKafkaTemplate.send("vote-paper", votePaperDTO.roomId, it)
    }
  }
}