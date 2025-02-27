package vottega.vote_service.exception

class VoteStatusConflictException(message: String) : RuntimeException(message)

class VoteForbiddenException(message: String) : RuntimeException(message)