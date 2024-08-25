package vottega.vote_service.exception

class VoteNotFoundException(voteId : Long) : RuntimeException("$voteId : Vote not found with id")