package pt.ipleiria.estg.dei.pi.mymultiprev.data.network.requests

import com.squareup.moshi.Json

data class AuthToken(
    @Json(name = "id")
    val id: String?,
    @Json(name = "username")
    val username: String?,
    @Json(name = "token")
    val token: String?,
    @Json(name = "expiresAt")
    val expiresAt: String?
)