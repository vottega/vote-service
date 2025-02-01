package vottega.vote_service.domain

import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import vottega.vote_service.domain.enum.VotePaperType
import java.time.LocalDateTime
import java.util.*

@Entity
@SQLDelete(sql = "UPDATE participant SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
class VotePaper(
  val userId: UUID,
  @ManyToOne
  @JoinColumn(name = "vote_id")
  val vote: Vote,
  val userName: String
) {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long? = null

  var createdAt: LocalDateTime? = null
    private set
  var votedAt: LocalDateTime? = null
    private set

  var votePaperType: VotePaperType = VotePaperType.NOT_VOTED

  @PrePersist
  private fun prePersist() {
    createdAt = LocalDateTime.now()
  }

  fun vote(voteResultType: VotePaperType) {
    this.votePaperType = voteResultType
    this.votedAt = LocalDateTime.now()
  }
}