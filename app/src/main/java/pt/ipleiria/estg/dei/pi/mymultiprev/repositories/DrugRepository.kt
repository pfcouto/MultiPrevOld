package pt.ipleiria.estg.dei.pi.mymultiprev.repositories

import kotlinx.coroutines.flow.Flow
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos.DrugDao
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.ServiceBuilder
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.mappers.DrugNetworkMapper
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.networkBoundResource
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.services.DrugService
import javax.inject.Inject

class DrugRepository @Inject constructor(
    private val drugDao: DrugDao,
    private val drugNetworkMapper: DrugNetworkMapper,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : BaseRepository {
    fun getDrugs(patientId: String): Flow<Resource<List<Drug>>> {
        val drugServices =
            ServiceBuilder(sharedPreferencesRepository).buildService(DrugService::class.java)
        return networkBoundResource(
            query = {
                drugDao.getDrugs()
            },
            fetch = {
                drugServices.getDrugs(patientId)
            },
            saveFetchResult = { drugs ->
                drugDao.insertDrugs(
                    drugNetworkMapper.mapFromEntityList(
                        drugs
                    )
                )
            }
        )
    }

    suspend fun setDrugAlias(id: String, alias: String) {
        return drugDao.setDrugAlias(id, alias)
    }

    fun getDrugById(drugId: String): Flow<Resource<Drug>> {
        val drugServices =
            ServiceBuilder(sharedPreferencesRepository).buildService(DrugService::class.java)
        return networkBoundResource(
            query = {
                drugDao.getDrugById(drugId)
            },
            fetch = {
                drugServices.getDrugById(drugId)
            },
            saveFetchResult = { drug ->
                drugDao.insertDrug(
                    drugNetworkMapper.mapFromDTO(
                        drug
                    )
                )
            }
        )
    }

    override suspend fun deleteData() {
        drugDao.deleteAll()
    }
}