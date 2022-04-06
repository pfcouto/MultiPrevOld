package pt.ipleiria.estg.dei.pi.mymultiprev.data.network.mappers

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toLocalDateTime
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos.DrugDao
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos.DrugDTO
import javax.inject.Inject

class DrugNetworkMapper @Inject constructor(
    private val drugDao: DrugDao
) : EntityMapper<DrugDTO, Drug> {
    override fun mapFromDTO(dto: DrugDTO): Drug {
        val pId = dto.id
        val alias = runBlocking {
            drugDao.getDrugAlias(pId)
        }

        return Drug(
            id = dto.id,
            name = dto.name,
            alias = alias ?: dto.name,
            intakeMethod = dto.intakeMethod,
            commercialName = dto.commercialName,
            createAt = dto.createdAt.toLocalDateTime(),
            updatedAt = dto.updatedAt.toLocalDateTime(),
            pharmClass = dto.pharmClass,
            therapies = dto.therapies,
            dosageMassMg = dto.dosageMassMg,
            dosageVolumeMl = dto.dosageVolumeMl
        )
    }

    override fun mapFromEntity(entity: Drug): DrugDTO {
        return DrugDTO(
            entity.id,
            entity.name,
            entity.commercialName,
            entity.intakeMethod,
            entity.createAt.toString(),
            entity.updatedAt.toString(),
            entity.pharmClass,
            entity.therapies,
            entity.dosageMassMg,
            entity.dosageVolumeMl
        )
    }

    fun mapFromEntityList(entities: List<DrugDTO>): List<Drug> {
        return entities.map { mapFromDTO(it) }
    }
}