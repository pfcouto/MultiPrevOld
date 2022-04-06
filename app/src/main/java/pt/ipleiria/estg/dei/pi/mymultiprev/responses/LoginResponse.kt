package pt.ipleiria.estg.dei.pi.mymultiprev.responses

data class LoginResponse(
    val id: String,
    val username: String,
    val token: String,
    val expiresAt: Any?
)