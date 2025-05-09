package vottega.vote_service.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

  @ExceptionHandler(VoteNotFoundException::class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  fun handleVoteNotFoundException(e: VoteNotFoundException) = e.message

  @ExceptionHandler(VoteStatusConflictException::class)
  @ResponseStatus(HttpStatus.CONFLICT)
  fun handleVoteStatusConflictException(e: VoteStatusConflictException) = e.message

  @ExceptionHandler(VoteForbiddenException::class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  fun handleVoteForbiddenException(e: VoteForbiddenException) = e.message

}