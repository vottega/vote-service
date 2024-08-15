package vottega.vote_service.domain

import jakarta.persistence.Embeddable

@Embeddable
data class FractionVO(val numerator : Int, val denominator: Int) {
    fun multipy(number : Int): Double {
        return (number * numerator).toDouble() / denominator.toDouble()
    }
}
