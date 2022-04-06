package pt.ipleiria.estg.dei.pi.mymultiprev.data.network.mappers

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toLocalDateTime
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos.PrescriptionItemDao
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos.PrescriptionItemDTO
import javax.inject.Inject

class PrescriptionItemNetworkMapper @Inject constructor(
    private val prescriptionItemDao: PrescriptionItemDao
) :
    EntityMapper<PrescriptionItemDTO, PrescriptionItem> {

    override fun mapFromDTO(dto: PrescriptionItemDTO): PrescriptionItem {
        val pId = dto.id
        val pair = runBlocking {
            Pair<String?, Boolean?>(
                prescriptionItemDao.getPrescriptionItemPhoto(pId),
                prescriptionItemDao.getPrescriptionItemAlarm(pId)
            )
        }
        return PrescriptionItem(
            id = dto.id,
            drug = dto.drug,
            intakeValue = dto.intakeValue,
            frequency = dto.frequency,
            pathology = dto.pathology,
            intakeUnit = dto.intakeUnit,
            acquiredAt = dto.acquiredAt?.toLocalDateTime(),
            nextIntake = dto.nextIntake?.toLocalDateTime(),
            prescription = dto.prescription,
            status = dto.status,
            expectedIntakeCount = dto.expectedIntakeCount,
            intakesTakenCount = dto.intakesTakenCount,
            imageLocation = pair.first,
            alarm = pair.second ?: true
        )
    }

    override fun mapFromEntity(entity: PrescriptionItem): PrescriptionItemDTO {
        return PrescriptionItemDTO(
            id = entity.id,
            drug = entity.drug,
            prescription = entity.prescription,
            frequency = entity.frequency,
            pathology = entity.pathology,
            nextIntake = entity.nextIntake.toString(),
            intakeValue = entity.intakeValue,
            intakeUnit = entity.intakeUnit,
            acquiredAt = entity.acquiredAt.toString(),
            status = entity.status,
            expectedIntakeCount = entity.expectedIntakeCount,
            intakesTakenCount = entity.intakesTakenCount
        )
    }

    fun mapFromEntityList(entities: List<PrescriptionItemDTO>): List<PrescriptionItem> {
        return entities.map { mapFromDTO(it) }
    }
}