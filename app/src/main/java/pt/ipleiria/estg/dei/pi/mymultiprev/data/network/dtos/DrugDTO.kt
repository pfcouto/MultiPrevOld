package pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos

import com.squareup.moshi.Json

data class DrugDTO(
    @Json(name = "id")
    var id: String,
    @Json(name = "name")
    var name: String,
    @Json(name = "commercialName")
    var commercialName: String,
    @Json(name = "intakeMethod")
    var intakeMethod: String,
    @Json(name = "createdAt")
    var createdAt: String,
    @Json(name = "updatedAt")
    var updatedAt: String,
    @Json(name = "pharmClass")
    var pharmClass: String,
    @Json(name = "therapies")
    var therapies: String,
    @Json(name = "dosageMassMg")
    var dosageMassMg: Int,
    @Json(name = "dosageVolumeMl")
    var dosageVolumeMl: Int
)
