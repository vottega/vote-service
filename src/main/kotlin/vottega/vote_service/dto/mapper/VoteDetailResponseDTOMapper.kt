package vottega.vote_service.dto.mapper

import org.springframework.stereotype.Service
import vottega.vote_service.domain.Vote
import vottega.vote_service.domain.enum.VotePaperType
import vottega.vote_service.dto.ParticipantIdNameDTO
import vottega.vote_service.dto.VoteDetailResponseDTO

@Service
class VoteDetailResponseDTOMapper {
  fun toVoteDetailResponse(vote: Vote): VoteDetailResponseDTO {
    if (vote.isSecret) {
      return VoteDetailResponseDTO(
        id = vote.id,
        agendaName = vote.agendaName,
        voteName = vote.voteName,
        status = vote.status,
        createdAt = vote.createdAt,
        startedAt = vote.startedAt,
        finishedAt = vote.finishedAt,
        passRate = vote.passRate,
        minParticipantNumber = vote.minParticipantNumber,
        minParticipantRate = vote.minParticipantRate,
        isSecret = vote.isSecret,
        result = vote.result,
        yesList = List(vote.votePaperList.filter { it.votePaperType == VotePaperType.YES }.size) { index ->
          ParticipantIdNameDTO(
            id = null,
            name = "anonymous user$index"
          )
        },
        noList = List(vote.votePaperList.filter { it.votePaperType == VotePaperType.NO }.size) { index ->
          ParticipantIdNameDTO(
            id = null,
            name = "anonymous user$index"
          )
        },
        abstainList = List(vote.votePaperList.filter { it.votePaperType == VotePaperType.ABSTAIN }.size) { index ->
          ParticipantIdNameDTO(
            id = null,
            name = "anonymous user$index"
          )
        },
      )
    }

    return VoteDetailResponseDTO(
      id = vote.id,
      agendaName = vote.agendaName,
      voteName = vote.voteName,
      status = vote.status,
      createdAt = vote.createdAt,
      startedAt = vote.startedAt,
      finishedAt = vote.finishedAt,
      passRate = vote.passRate,
      minParticipantNumber = vote.minParticipantNumber,
      minParticipantRate = vote.minParticipantRate,
      isSecret = vote.isSecret,
      result = vote.result,
      yesList = vote.votePaperList.filter { it.votePaperType == VotePaperType.YES }.map {
        ParticipantIdNameDTO(
          id = it.userId,
          name = it.userName
        )
      },
      noList = vote.votePaperList.filter { it.votePaperType == VotePaperType.NO }.map {
        ParticipantIdNameDTO(
          id = it.userId,
          name = it.userName
        )
      },
      abstainList = vote.votePaperList.filter { it.votePaperType == VotePaperType.ABSTAIN }.map {
        ParticipantIdNameDTO(
          id = it.userId,
          name = it.userName
        )
      },
    )
  }
}
