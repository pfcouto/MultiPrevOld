package pt.ipleiria.estg.dei.pi.mymultiprev.data.network.mappers

import kotlinx.datetime.toLocalDateTime
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Patient
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos.PatientDTO
import javax.inject.Inject

class PatientNetworkMapper @Inject constructor() : EntityMapper<PatientDTO, Patient> {
    override fun mapFromDTO(dto: PatientDTO): Patient {
        return Patient(
            id = dto.id,
            name = dto.name,
            username = null,
            patientNumber = dto.patientNumber,
            gender = dto.gender,
            birthdate = dto.birthdate.toLocalDateTime(),
            cellphone = null,
        )
    }

    override fun mapFromEntity(entity: Patient): PatientDTO {
        return PatientDTO(
            id = entity.id,
            name = entity.name,
            patientNumber = entity.patientNumber,
            gender = entity.gender!!,
            birthdate = entity.birthdate.toString(),
        )
    }
}