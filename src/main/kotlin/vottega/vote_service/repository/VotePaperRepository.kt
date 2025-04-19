package vottega.vote_service.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import vottega.vote_service.domain.VotePaper

@Repository
interface VotePaperRepository : JpaRepository<VotePaper, Long>