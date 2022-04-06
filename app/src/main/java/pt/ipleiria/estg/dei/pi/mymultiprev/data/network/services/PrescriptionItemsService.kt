package pt.ipleiria.estg.dei.pi.mymultiprev.data.network.services

import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos.IntakeDTO
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos.PrescriptionItemDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PrescriptionItemsService {
    @GET("patients/{patientId}/prescriptionItems/active")
    suspend fun getActivePrescriptionItems(@Path("patientId") id: String): List<PrescriptionItemDTO>

    @GET("patients/{patientId}/prescriptionItems/complete")
    suspend fun getCompletedPrescriptionItems(@Path("patientId") id: String): List<PrescriptionItemDTO>

    @POST("prescriptionItems/{prescriptionId}/")
    suspend fun updatePrescription(
        @Path("prescriptionId") id: String,
        @Body prescription: PrescriptionItemDTO
    ): PrescriptionItemDTO

    @GET("prescriptionItems/{prescriptionItemId}/intakes")
    suspend fun getIntakesByPrescriptionItemId(@Path("prescriptionItemId") id: String): List<IntakeDTO>
}