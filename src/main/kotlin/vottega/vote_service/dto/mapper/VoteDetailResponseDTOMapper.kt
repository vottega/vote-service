package vottega.vote_service.dto.mapper

import org.springframework.stereotype.Service
import vottega.room_service.dto.RoomResponseDTO
import vottega.vote_service.domain.Vote
import vottega.vote_service.domain.enum.VotePaperType
import vottega.vote_service.dto.ParticipantResponseDTO
import vottega.vote_service.dto.VoteDetailResponseDTO

@Service
class VoteDetailResponseDTOMapper {
    fun toVoteDetailResponse(vote: Vote, room: RoomResponseDTO): VoteDetailResponseDTO {
        if(vote.isSecret){
            return VoteDetailResponseDTO(
                id = vote.id,
                title = vote.title,
                status = vote.status,
                createdAt = vote.createdAt,
                startedAt = vote.startedAt,
                finishedAt = vote.finishedAt,
                passRate = vote.passRate,
                result = vote.result,
                yesList = List(vote.votePaperList.filter { it.voteResultType == VotePaperType.YES }.size) { index ->
                    ParticipantResponseDTO(
                        id = null,
                        name = "anonymous user$index"
                    )
                },
                noList = List(vote.votePaperList.filter { it.voteResultType == VotePaperType.NO }.size) { index ->
                    ParticipantResponseDTO(
                        id = null,
                        name = "anonymous user$index"
                    )
                },
                abstainList = List(vote.votePaperList.filter { it.voteResultType == VotePaperType.ABSTAIN }.size) { index ->
                    ParticipantResponseDTO(
                        id = null,
                        name = "anonymous user$index"
                    )
                },
            )
        }

        val participantMap = room.participants.associateBy { it.id }
        return VoteDetailResponseDTO(
            id = vote.id,
            title = vote.title,
            status = vote.status,
            createdAt = vote.createdAt,
            startedAt = vote.startedAt,
            finishedAt = vote.finishedAt,
            passRate = vote.passRate,
            result = vote.result,
            yesList = vote.votePaperList.filter { it.voteResultType == VotePaperType.YES }.map {
                ParticipantResponseDTO(
                    id = it.userId,
                    name = participantMap[it.userId]?.name ?: "Unknown User"
                )
            },
            noList = vote.votePaperList.filter { it.voteResultType == VotePaperType.NO }.map {
                ParticipantResponseDTO(
                    id = it.userId,
                    name = participantMap[it.userId]?.name ?: "Unknown User"
                )
            },
            abstainList = vote.votePaperList.filter { it.voteResultType == VotePaperType.ABSTAIN }.map {
                ParticipantResponseDTO(
                    id = it.userId,
                    name = participantMap[it.userId]?.name ?: "Unknown User"
                )
            },
        )
    }
}
