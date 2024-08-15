package vottega.vote_service.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import vottega.vote_service.domain.Vote

@Repository
interface VoteRepository : JpaRepository<Vote, Long> {
    @Query("SELECT v FROM Vote v JOIN FETCH VotePaper WHERE v.roomId = :roomId")
    fun findByRoomId(roomId: Long): List<Vote>
}