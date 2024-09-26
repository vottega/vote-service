import vottega.vote_service.avro.VoteDetailResponseDTO as AvroVoteDetailResponseDTO
import vottega.vote_service.avro.ParticipantResponseDTO as AvroParticipantResponseDTO
import vottega.vote_service.domain.FractionVO
import vottega.vote_service.dto.VoteDetailResponseDTO
import vottega.vote_service.dto.ParticipantResponseDTO
import java.time.ZoneOffset

class AvroMapper {
    fun toAvro(voteDetailDTO: VoteDetailResponseDTO): AvroVoteDetailResponseDTO {
        return AvroVoteDetailResponseDTO.newBuilder()
            .setId(voteDetailDTO.id ?: 0L)
            .setTitle(voteDetailDTO.title)
            .setStatus(voteDetailDTO.status.name)
            .setCreatedAt(voteDetailDTO.createdAt?.toInstant(ZoneOffset.UTC)?.toEpochMilli() ?: 0L)
            .setStartedAt(voteDetailDTO.startedAt?.toInstant(ZoneOffset.UTC)?.toEpochMilli() ?: 0L)
            .setFinishedAt(voteDetailDTO.finishedAt?.toInstant(ZoneOffset.UTC)?.toEpochMilli() ?: 0L)
            .setPassRate(
                AvroVoteDetailResponseDTO.FractionVO.newBuilder()
                    .setNumerator(voteDetailDTO.passRate.numerator)
                    .setDenominator(voteDetailDTO.passRate.denominator)
                    .build()
            )
            .setResult(voteDetailDTO.result?.name)
            .setYesList(voteDetailDTO.yesList.map { toAvro(it) })
            .setNoList(voteDetailDTO.noList.map { toAvro(it) })
            .setAbstainList(voteDetailDTO.abstainList.map { toAvro(it) })
            .build()
    }

    fun toAvro(participantDTO: ParticipantResponseDTO): AvroParticipantResponseDTO {
        return AvroParticipantResponseDTO.newBuilder()
            .setId(participantDTO.id?.toString() ?: "")
            .setName(participantDTO.name ?: "Unknown")
            .build()
    }
}