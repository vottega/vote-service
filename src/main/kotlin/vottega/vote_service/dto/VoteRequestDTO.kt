package vottega.vote_service.dto

data class VoteRequestDTO(val title : String, val passRateNumerator: Int?, val passRateDenominator: Int?, val isSecret : Boolean?)
