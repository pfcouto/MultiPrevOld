package pt.ipleiria.estg.dei.pi.mymultiprev.data.network.services

import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos.IntakeDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface IntakeService {
    @POST("intakes")
    suspend fun doIntake(@Body intake: IntakeDTO): IntakeDTO
}