package pt.ipleiria.estg.dei.pi.mymultiprev.data.network.services

import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Symptom
import pt.ipleiria.estg.dei.pi.mymultiprev.responses.SymptomTypeItemResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SymptomsService {
    @GET("symptomTypes")
    suspend fun getAllSymptomTypes(): List<SymptomTypeItemResponse>

    @POST("symptoms")
    suspend fun registerSymptoms(@Body symptomsList: List<Symptom>)
}