package vottega.vote_service.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import vottega.vote_service.domain.Vote
import vottega.vote_service.domain.enum.Status
import java.time.LocalDateTime

@Repository
interface VoteRepository : JpaRepository<Vote, Long> {
  @Query("SELECT v FROM Vote v JOIN FETCH v.votePaperList  WHERE v.roomId = :roomId")
  fun findByRoomId(roomId: Long): List<Vote>
  fun findByStatusAndReservedStartTimeLessThanEqual(
    status: Status,
    reservedStartTimeIsLessThan: LocalDateTime
  ): MutableList<Vote>
}