package pt.ipleiria.estg.dei.pi.mymultiprev.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.toLocalDateTime
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos.IntakeDao
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos.PrescriptionItemDao
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Intake
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.enums.PrescriptionItemStatus
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.ServiceBuilder
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos.IntakeDTO
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.mappers.IntakeNetworkMapper
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.networkBoundResource
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.services.IntakeService
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.services.PrescriptionItemsService
import javax.inject.Inject

class IntakeRepository @Inject constructor(
    private val intakeDao: IntakeDao,
    private val intakeNetworkMapper: IntakeNetworkMapper,
    private val prescriptionItemDao: PrescriptionItemDao,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : BaseRepository {

    fun getIntakesByPrescriptionItemId(prescriptionItemId: String): Flow<Resource<List<Intake>>> {
        val prescriptionItemsServices =
            ServiceBuilder(sharedPreferencesRepository).buildService(PrescriptionItemsService::class.java)
        return networkBoundResource(
            query = {
                intakeDao.getIntakesByPrescriptionItemId(prescriptionItemId)
            },
            fetch = {
                prescriptionItemsServices.getIntakesByPrescriptionItemId(prescriptionItemId)
            },
            saveFetchResult = { intakes ->
                intakeDao.insertIntakes(
                    intakeNetworkMapper.mapFromEntityList(
                        intakes
                    )
                )
            }
        )
    }

    suspend fun doIntake(intakeDTO: IntakeDTO): Resource<IntakeDTO> {
        val intakeService =
            ServiceBuilder(sharedPreferencesRepository).buildService(IntakeService::class.java)
        val result = safeApiCall { intakeService.doIntake(intakeDTO) }
        if (result is Resource.Success<*>) {
            prescriptionItemDao.updateNextIntake(
                result.data!!.prescriptionItemId,
                result.data.expectedAt!!.toLocalDateTime()
            )
            val prescription =
                prescriptionItemDao.getPrescriptionItemById(result.data.prescriptionItemId)
            if (prescription.expectedIntakeCount == prescription.intakesTakenCount!! + 1)
                prescriptionItemDao.updateStatus(
                    prescription.id, PrescriptionItemStatus.Completed.value(),
                    prescription.expectedIntakeCount!!
                )
        }
        return result
    }

    override suspend fun deleteData() {
        intakeDao.deleteAll()
    }
}