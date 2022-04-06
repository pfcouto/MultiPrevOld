package pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos

import com.squareup.moshi.Json

data class IntakeDTO(
    @Json(name = "id")
    var id: String?,
    @Json(name = "patientId")
    var patientId: String,
    @Json(name = "prescriptionItemId")
    var prescriptionItemId: String,
    @Json(name = "intakeDate")
    var intakeDate: String?,
    @Json(name = "expectedAt")
    var expectedAt: String?,
    @Json(name = "took")
    var took: Boolean,
)