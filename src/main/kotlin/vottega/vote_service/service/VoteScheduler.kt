package vottega.vote_service.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import vottega.vote_service.domain.enum.Status
import vottega.vote_service.repository.VoteRepository
import java.time.LocalDateTime

@Component
class VoteScheduler(
  private val voteService: VoteService,
  private val voteRepository: VoteRepository,
) {
  @Scheduled(fixedDelay = 60000)
  fun checkReservedVote() {
    val votes = voteRepository.findByStatusAndReservedStartTimeLessThanEqual(Status.CREATED, LocalDateTime.now())
    votes.forEach {
      voteService.editVoteStatus(it.roomId, it.id!!, Status.STARTED)
    }
  }

}