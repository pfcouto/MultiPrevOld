package pt.ipleiria.estg.dei.pi.mymultiprev.repositories

import kotlinx.coroutines.flow.Flow
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos.AuthDao
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Patient
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.ServiceBuilder
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.mappers.PatientNetworkMapper
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.networkBoundResource
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.requests.LoginRequest
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.services.AuthService
import pt.ipleiria.estg.dei.pi.mymultiprev.responses.LoginResponse
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authDao: AuthDao,
    private val patientNetworkMapper: PatientNetworkMapper,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : BaseRepository {

    suspend fun login(loginRequest: LoginRequest): Resource<LoginResponse> {
        val authService =
            ServiceBuilder(sharedPreferencesRepository).buildService(AuthService::class.java)
        return safeApiCall {
            authService.login(loginRequest)
        }
    }

    suspend fun getPatient(id: String): Flow<Resource<Patient>> {
        val authService =
            ServiceBuilder(sharedPreferencesRepository).buildService(AuthService::class.java)
        return networkBoundResource(
            query = {
                authDao.getPatient(id)
            },
            fetch = {
                authService.getPatient(id)
            },
            saveFetchResult = { patient ->
                authDao.setPatient(
                    patientNetworkMapper.mapFromDTO(
                        patient
                    )
                )
            }
        )
    }

    override suspend fun deleteData() {
        authDao.deleteAll()
    }
}