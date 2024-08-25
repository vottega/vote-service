package vottega.vote_service.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(VoteNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleVoteNotFoundException(e: VoteNotFoundException) = e.message

    @ExceptionHandler(VoteStatusConflictException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleVoteStatusConflictException(e: VoteStatusConflictException) = e.message

}