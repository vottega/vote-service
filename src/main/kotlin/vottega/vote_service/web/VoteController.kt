package vottega.vote_service.web

import org.springframework.web.bind.annotation.*
import vottega.vote_service.domain.enum.VotePaperType
import vottega.vote_service.service.VoteService
import java.util.*

@RestController
@RequestMapping("api/vote")
class VoteController(private val voteService: VoteService) {
    @PostMapping("/{voteId}/{action}")
    fun actionVote(@PathVariable voteId: Long, @PathVariable action: String) {
        when (action) {
            "start" -> voteService.startVote(voteId)
            "end" -> voteService.endVote(voteId)
        }
    }

    @PostMapping("/{roomId}")
    fun createVote(@PathVariable roomId: Long, title: String, passRateNumerator: Int?, passRateDenominator: Int?) {
        TODO("이거 DTO로 받아서 넘겨주기")
        voteService.createVote(title, roomId, passRateNumerator, passRateDenominator)
    }

    @GetMapping("/{roomId}")
    fun getVoteInfo(@PathVariable roomId: Long) = voteService.getVoteInfo(roomId)

    @GetMapping("/{voteId}/detail")
    fun getVoteDetail(@PathVariable voteId: Long) = voteService.getVoteDetail(voteId)

    @PutMapping("/{voteId}")
    fun addVotePaper(@PathVariable voteId: Long, userId: UUID, voteResultType: VotePaperType) {
        voteService.addVotePaper(voteId, userId, voteResultType)
    }

}