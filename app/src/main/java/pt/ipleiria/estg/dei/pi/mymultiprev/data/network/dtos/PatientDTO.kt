package pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos

import com.squareup.moshi.Json

data class PatientDTO(
    @Json(name = "id")
    var id: String,
    @Json(name = "name")
    var name: String,
    @Json(name = "patientNumber")
    var patientNumber: Int,
    @Json(name = "birthDate")
    var birthdate: String,
    @Json(name = "gender")
    var gender: String
)