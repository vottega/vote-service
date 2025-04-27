package vottega.vote_service.security

import org.springframework.stereotype.Component
import vottega.vote_service.exception.VoteNotFoundException
import vottega.vote_service.repository.VoteRepository
import vottega.vote_service.service.cache.RoomOwnerService
import vottega.vote_service.service.cache.RoomParticipantService
import java.util.*

@Component
class VoteSecurity(
  private val voteRepository: VoteRepository,
  private val roomOwnerService: RoomOwnerService,
  private val roomParticipantService: RoomParticipantService
) {
  fun isOwner(ownerId: Long, voteId: Long): Boolean {
    val vote = voteRepository.findById(voteId)
      .orElseThrow { VoteNotFoundException(voteId) }
    return roomOwnerService.getRoomOwner(vote.roomId) == ownerId
  }

  fun isParticipantInRoom(roomId: Long, participantId: UUID): Boolean {
    return roomParticipantService.getRoomParticipant(roomId, participantId) != null
  }

  fun isParticipantInVote(voteId: Long, participantId: UUID): Boolean {
    val vote = voteRepository.findById(voteId)
      .orElseThrow { VoteNotFoundException(voteId) }
    return roomParticipantService.getRoomParticipant(vote.roomId, participantId) != null
  }
}