package vottega.vote_service.dto.mapper

import org.springframework.stereotype.Component
import vottega.vote_service.avro.FractionAvro
import vottega.vote_service.domain.FractionVO

@Component
class FractionMapper {
  fun toFractionAvro(fractionDTO: FractionVO): FractionAvro {
    return FractionAvro.newBuilder()
      .setNumerator(fractionDTO.numerator)
      .setDenominator(fractionDTO.denominator)
      .build()
  }
}