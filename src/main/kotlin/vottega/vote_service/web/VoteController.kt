package vottega.vote_service.web

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import vottega.vote_service.argument_resolver.ParticipantId
import vottega.vote_service.domain.enum.Status
import vottega.vote_service.domain.enum.VotePaperType
import vottega.vote_service.dto.VoteRequestDTO
import vottega.vote_service.service.VoteService
import java.util.*

@RestController
@RequestMapping("/api/vote")
@Tag(name = "Vote Controller", description = "투표 관련 API")
class VoteController(private val voteService: VoteService) {
  @PostMapping("/{voteId}/{action}")
  @Operation(summary = "투표 상태 변경", description = "투표 상태를 변경합니다.")
  fun actionVote(@PathVariable voteId: Long, @PathVariable action: Status) =
    voteService.editVoteStatusWithSecurity(voteId, action)


  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{roomId}")
  @Operation(summary = "투표 생성", description = "투표를 생성합니다.")
  fun createVote(@PathVariable roomId: Long, @RequestBody voteRequestDTO: VoteRequestDTO) =
    voteService.createVote(roomId, voteRequestDTO)


  @GetMapping("/{roomId}")
  @Operation(summary = "투표 정보 조회", description = "투표 정보를 조회합니다.")
  fun getVoteInfo(@PathVariable roomId: Long) = voteService.getVoteInfo(roomId)


  @GetMapping("/{voteId}/detail")
  @Operation(summary = "투표 상세 조회", description = "투표 상세 정보를 조회합니다.")
  fun getVoteDetail(@PathVariable voteId: Long) = voteService.getVoteDetail(voteId)

  @ResponseStatus(HttpStatus.CREATED)
  @PutMapping("/{voteId}")
  @Operation(summary = "투표", description = "투표를 합니다.")
  fun addVotePaper(
    @PathVariable voteId: Long,
    @ParticipantId userId: UUID,
    voteResultType: VotePaperType
  ) =
    voteService.addVotePaper(voteId, userId, voteResultType)

  @PostMapping("/{voteId}/reset")
  @Operation(summary = "투표 초기화", description = "투표를 초기화합니다.")
  fun resetVote(@PathVariable voteId: Long) = voteService.resetVote(voteId)


}