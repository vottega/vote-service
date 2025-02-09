package vottega.vote_service.service

import vottega.vote_service.domain.enum.Status
import vottega.vote_service.domain.enum.VotePaperType
import vottega.vote_service.dto.VoteDetailResponseDTO
import vottega.vote_service.dto.VoteRequestDTO
import vottega.vote_service.dto.VoteResponseDTO
import java.util.*

interface VoteService {
  fun createVote(roomId: Long, voteRequestDTO: VoteRequestDTO): VoteDetailResponseDTO

  fun editVote(roomId: Long, voteRequestDTO: VoteRequestDTO)
  fun editVoteStatus(voteId: Long, action: Status): VoteDetailResponseDTO
  fun addVotePaper(voteId: Long, userId: UUID, voteResultType: VotePaperType)
  fun getVoteInfo(roomId: Long): List<VoteResponseDTO>
  fun getVoteDetail(voteId: Long): VoteDetailResponseDTO

  fun resetVote(voteId: Long)
}