package vottega.vote_service.dto.mapper

import org.springframework.stereotype.Service
import vottega.vote_service.domain.Vote
import vottega.vote_service.domain.enum.VotePaperType
import vottega.vote_service.dto.VoteResponseDTO

@Service
class VoteResponseDTOMapper {
    fun mapToResponse(vote: Vote): VoteResponseDTO {
        return VoteResponseDTO(
            id = vote.id,
            agendaName = vote.agendaName,
            voteName = vote.voteName,
            status = vote.status,
            createdAt = vote.createdAt,
            passRate = vote.passRate,
            minParticipantNumber = vote.minParticipantNumber,
            minParticipantRate = vote.minParticipantRate,
            isSecret = vote.isSecret,
            yesNum = vote.votePaperList.count { it.voteResultType == VotePaperType.YES },
            noNum = vote.votePaperList.count { it.voteResultType == VotePaperType.NO },
            abstainNum = vote.votePaperList.count { it.voteResultType == VotePaperType.ABSTAIN },
            result = vote.result
        )
    }
}