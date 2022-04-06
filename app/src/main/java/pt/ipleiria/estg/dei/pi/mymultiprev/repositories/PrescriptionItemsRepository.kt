package pt.ipleiria.estg.dei.pi.mymultiprev.repositories

import android.net.Uri
import androidx.core.net.toUri
import kotlinx.coroutines.flow.Flow
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos.PrescriptionItemDao
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.ServiceBuilder
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos.PrescriptionItemDTO
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.mappers.PrescriptionItemNetworkMapper
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.networkBoundResource
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.services.PrescriptionItemsService
import javax.inject.Inject

class PrescriptionItemsRepository @Inject constructor(
    private val prescriptionItemDao: PrescriptionItemDao,
    private val prescriptionItemNetworkMapper: PrescriptionItemNetworkMapper,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : BaseRepository {
    fun getActivePrescriptionItems(patientId: String): Flow<Resource<List<PrescriptionItem>>> {
        val prescriptionItemsServices =
            ServiceBuilder(sharedPreferencesRepository).buildService(PrescriptionItemsService::class.java)
        return networkBoundResource(
            query = {
                prescriptionItemDao.getPrescriptionItems()
            },
            fetch = {
                prescriptionItemsServices.getActivePrescriptionItems(patientId)
            },
            saveFetchResult = { prescriptionItems ->
                prescriptionItemDao.insertPrescriptionItems(
                    prescriptionItemNetworkMapper.mapFromEntityList(
                        prescriptionItems
                    )
                )
            }
        )
    }

    fun getCompletedPrescriptionItems(patientId: String): Flow<Resource<List<PrescriptionItem>>> {
        val prescriptionItemsServices =
            ServiceBuilder(sharedPreferencesRepository).buildService(PrescriptionItemsService::class.java)

        return networkBoundResource(
            query = {
                prescriptionItemDao.getCompletedPrescriptionItems()
            },
            fetch = {
                prescriptionItemsServices.getCompletedPrescriptionItems(patientId)
            },
            saveFetchResult = { prescriptionItems ->
                prescriptionItemDao.insertPrescriptionItems(
                    prescriptionItemNetworkMapper.mapFromEntityList(
                        prescriptionItems
                    )
                )
            }
        )
    }

    suspend fun updatePrescription(
        prescriptionId: String,
        prescriptionItem: PrescriptionItem
    ): Resource<PrescriptionItemDTO> {
        val prescriptionItemsServices =
            ServiceBuilder(sharedPreferencesRepository).buildService(PrescriptionItemsService::class.java)
        val result = safeApiCall {
            prescriptionItemsServices.updatePrescription(
                prescriptionId,
                prescriptionItemNetworkMapper.mapFromEntity(prescriptionItem)
            )
        }
        if (result is Resource.Success && result.data != null) {
            prescriptionItemDao.updatePrescriptionItem(
                prescriptionItemNetworkMapper.mapFromDTO(
                    result.data
                )
            )
        }
        return result
    }

    suspend fun getPrescriptionItemPhoto(id: String): Uri? {
        val string = prescriptionItemDao.getPrescriptionItemPhoto(id) ?: return null
        return string.toUri()
    }

    suspend fun setPrescriptionItemPhoto(id: String, photo_uri: Uri) {
        return prescriptionItemDao.setPrescriptionItemPhoto(id, photo_uri.toString())
    }

    suspend fun getLocalPrescriptionItem(specificPrescriptionItemId: String): PrescriptionItem {
        return prescriptionItemDao.getPrescriptionItemById(specificPrescriptionItemId)
    }

    override suspend fun deleteData() {
        prescriptionItemDao.deleteAll()
    }
}
