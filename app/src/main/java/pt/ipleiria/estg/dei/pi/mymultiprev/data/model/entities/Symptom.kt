package pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities

import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.enums.SymptomRegistrationSituation

data class Symptom(
    val registratedSituation: SymptomRegistrationSituation,
    val patientId: String,
    val typeId: String,
    val drugId: String,
    val registeredAt: String
)