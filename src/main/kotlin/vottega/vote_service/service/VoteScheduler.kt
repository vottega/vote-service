package vottega.vote_service.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import vottega.vote_service.domain.enum.Status
import vottega.vote_service.repository.VoteRepository
import java.time.LocalDateTime
import java.util.concurrent.Executor

@Component
class VoteScheduler(
  private val voteService: VoteService,
  private val voteRepository: VoteRepository,
  @Qualifier("taskExecutor")
  private val taskExecutor: Executor
) {
  @Scheduled(fixedDelay = 60000)
  fun checkReservedVote() {
    val votes = voteRepository.findByStatusAndReservedStartTimeBefore(Status.CREATED, LocalDateTime.now())
    votes.forEach {
      taskExecutor.execute {
        voteService.editVoteStatus(it.id!!, Status.STARTED)
      }
    }
  }

}