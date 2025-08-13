package vottega.vote_service.dto.mapper

import org.springframework.stereotype.Service
import vottega.avro.Result
import vottega.avro.VoteAction
import vottega.avro.VoteAvro
import vottega.avro.VoteStatus
import vottega.vote_service.domain.Vote
import vottega.vote_service.domain.enum.Status
import vottega.vote_service.domain.enum.VotePaperType
import vottega.vote_service.domain.enum.VoteResult
import vottega.vote_service.dto.VoteResponseDTO
import java.time.ZoneId

@Service
class VoteResponseDTOMapper(private val fractionMapper: FractionMapper) {
  fun toVoteResponseDTO(vote: Vote, isVoted: Boolean? = null): VoteResponseDTO {
    return VoteResponseDTO(
      id = vote.id ?: throw IllegalStateException("Vote ID is null"),
      roomId = vote.roomId,
      agendaName = vote.agendaName,
      voteName = vote.voteName,
      status = vote.status,
      createdAt = vote.createdAt ?: throw IllegalStateException("createdAt is null"),
      passRate = vote.passRate,
      minParticipantNumber = vote.minParticipantNumber,
      minParticipantRate = vote.minParticipantRate,
      reservedStartTime = vote.reservedStartTime,
      isSecret = vote.isSecret,
      yesNum = vote.votePaperList.count { it.votePaperType == VotePaperType.YES },
      noNum = vote.votePaperList.count { it.votePaperType == VotePaperType.NO },
      abstainNum = vote.votePaperList.count { it.votePaperType == VotePaperType.ABSTAIN },
      totalNum = vote.votePaperList.size,
      result = vote.result,
      startedAt = vote.startedAt,
      finishedAt = vote.finishedAt,
      lastUpdatedAt = vote.lastUpdatedAt ?: throw IllegalStateException("lastUpdatedAt is null"),
      isVoted = isVoted
    )
  }

  fun toVoteAvro(voteResponseDTO: VoteResponseDTO, voteAction: VoteAction): VoteAvro {
    return VoteAvro.newBuilder()
      .setId(voteResponseDTO.id)
      .setRoomId(voteResponseDTO.roomId)
      .setAgendaName(voteResponseDTO.agendaName)
      .setVoteName(voteResponseDTO.voteName)
      .setVoteStatus(statusToVoteStatus(voteResponseDTO.status))
      .setCreatedAt(voteResponseDTO.createdAt.atZone(ZoneId.systemDefault()).toInstant())
      .setPassRate(fractionMapper.toFractionAvro(voteResponseDTO.passRate))
      .setMinParticipantNumber(voteResponseDTO.minParticipantNumber)
      .setMinParticipantRate(fractionMapper.toFractionAvro(voteResponseDTO.minParticipantRate))
      .setIsSecret(voteResponseDTO.isSecret)
      .setReservedStartTime(voteResponseDTO.reservedStartTime?.atZone(ZoneId.systemDefault())?.toInstant())
      .setVoteAction(voteAction)
      .setStartedAt(voteResponseDTO.startedAt?.atZone(ZoneId.systemDefault())?.toInstant())
      .setFinishedAt(voteResponseDTO.finishedAt?.atZone(ZoneId.systemDefault())?.toInstant())
      .setLastUpdatedAt(voteResponseDTO.lastUpdatedAt.atZone(ZoneId.systemDefault()).toInstant())
      .setVoteResult(voteResultToResult(voteResponseDTO.result))
      .setYesNum(voteResponseDTO.yesNum)
      .setNoNum(voteResponseDTO.noNum)
      .setAbstainNum(voteResponseDTO.abstainNum)
      .build()
  }

  fun statusToVoteStatus(status: Status): VoteStatus {
    return when (status) {
      Status.CREATED -> VoteStatus.CREATED
      Status.STARTED -> VoteStatus.STARTED
      Status.ENDED -> VoteStatus.ENDED
    }
  }

  fun voteResultToResult(result: VoteResult): Result {
    return when (result) {
      VoteResult.PASSED -> Result.PASSED
      VoteResult.NOT_DECIDED -> Result.NOT_DECIDED
      VoteResult.REJECTED -> Result.REJECTED
    }
  }
}