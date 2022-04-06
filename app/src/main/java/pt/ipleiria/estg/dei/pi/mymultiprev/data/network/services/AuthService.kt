package pt.ipleiria.estg.dei.pi.mymultiprev.data.network.services

import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos.PatientDTO
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.requests.AuthToken
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.requests.LoginRequest
import pt.ipleiria.estg.dei.pi.mymultiprev.responses.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface AuthService {
    @Headers("client: mobile")
    @POST("auth/login")
    suspend fun login(
        @Body credentials: LoginRequest
    ): LoginResponse

    @POST("auth/refresh")
    fun refresh(
        @Body token: AuthToken
    ): Call<AuthToken>

    @GET("patients/{id}")
    suspend fun getPatient(@Path("id") id: String): PatientDTO
}