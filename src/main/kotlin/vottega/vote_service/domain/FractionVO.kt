package vottega.vote_service.domain

import jakarta.persistence.Embeddable

@Embeddable
data class FractionVO(val numerator: Int, val denominator: Int) {
  init {
    require(denominator != 0) { "Denominator cannot be zero" }
  }

  fun multiply(number: Int): Double {
    return (number * numerator).toDouble() / denominator.toDouble()
  }
}
