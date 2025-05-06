package vottega.vote_service.dto.mapper

import org.springframework.stereotype.Component
import vottega.vote_service.domain.Vote
import vottega.vote_service.dto.VoteDetailResponseDTO

@Component
class VoteDetailResponseDTOMapper(private val votePaperMapper: VotePaperMapper) {
  fun toVoteDetailResponse(vote: Vote): VoteDetailResponseDTO {
    return VoteDetailResponseDTO(
      id = vote.id ?: throw IllegalStateException("Vote ID is null"),
      roomId = vote.roomId,
      agendaName = vote.agendaName,
      voteName = vote.voteName,
      status = vote.status,
      createdAt = vote.createdAt ?: throw IllegalStateException("createdAt is null"),
      startedAt = vote.startedAt,
      finishedAt = vote.finishedAt,
      reservedAt = vote.reservedStartTime,
      passRate = vote.passRate,
      minParticipantNumber = vote.minParticipantNumber,
      minParticipantRate = vote.minParticipantRate,
      isSecret = vote.isSecret,
      result = vote.result,
      votePaperList = if (vote.isSecret) {
        vote.votePaperList.mapIndexed { index, votePaper -> votePaperMapper.toSecretVotePaperDTO(votePaper, index) }
      } else {
        vote.votePaperList.map { votePaperMapper.toVotePaperDTO(it) }
      },
    )
  }
}
