package pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos

import com.squareup.moshi.Json

data class PrescriptionItemDTO(
    @Json(name = "id")
    var id: String,
    @Json(name = "drugId")
    var drug: String,
    @Json(name = "prescriptionId")
    var prescription: String,
    @Json(name = "frequencyHours")
    var frequency: Int,
    @Json(name = "pathology")
    var pathology: String?,
    @Json(name = "nextIntake")
    var nextIntake: String?,
    @Json(name = "intakeValue")
    var intakeValue: Double,
    @Json(name = "intakeUnit")
    var intakeUnit: String,
    @Json(name = "acquiredAt")
    var acquiredAt: String?,
    @Json(name = "status")
    var status: Int?,
    @Json(name = "expectedIntakeCount")
    var expectedIntakeCount: Int?,
    @Json(name = "intakesTakenCount")
    var intakesTakenCount: Int?
)
