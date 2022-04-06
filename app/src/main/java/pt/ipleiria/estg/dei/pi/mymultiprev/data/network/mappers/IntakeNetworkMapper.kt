package pt.ipleiria.estg.dei.pi.mymultiprev.data.network.mappers

import kotlinx.datetime.toLocalDateTime
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Intake
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos.IntakeDTO
import javax.inject.Inject

class IntakeNetworkMapper @Inject constructor() :
    EntityMapper<IntakeDTO, Intake> {
    override fun mapFromDTO(dto: IntakeDTO): Intake {
        return Intake(
            id = dto.id!!,
            patientId = dto.patientId,
            prescriptionItemId = dto.prescriptionItemId,
            intakeDate = dto.intakeDate?.toLocalDateTime(),
            expectedAt = dto.expectedAt?.toLocalDateTime(),
            took = dto.took
        )
    }

    override fun mapFromEntity(entity: Intake): IntakeDTO {
        return IntakeDTO(
            id = entity.id,
            patientId = entity.patientId,
            prescriptionItemId = entity.prescriptionItemId,
            intakeDate = entity.intakeDate.toString(),
            expectedAt = entity.expectedAt.toString(),
            took = entity.took
        )
    }

    fun mapFromEntityList(entities: List<IntakeDTO>): List<Intake> {
        return entities.map { mapFromDTO(it) }
    }
}