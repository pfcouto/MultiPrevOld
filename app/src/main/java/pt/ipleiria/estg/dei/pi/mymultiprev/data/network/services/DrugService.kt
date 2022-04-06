package pt.ipleiria.estg.dei.pi.mymultiprev.data.network.services

import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos.DrugDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface DrugService {
    @GET("patients/{patientId}/drugs")
    suspend fun getDrugs(@Path("patientId") id: String): List<DrugDTO>

    @GET("drugs/{drugId}")
    suspend fun getDrugById(@Path("drugId") drugId: String): DrugDTO
}