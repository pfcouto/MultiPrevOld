package pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey
    var id: String,
    var name: String,
    var username: String?,
    var patientNumber: Int,
    var cellphone: Int?,
    var birthdate: LocalDateTime,
    var gender: String?,
)