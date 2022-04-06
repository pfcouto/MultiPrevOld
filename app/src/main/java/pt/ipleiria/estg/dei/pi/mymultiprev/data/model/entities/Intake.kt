package pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "intakes")
data class Intake(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    var patientId: String,
    var prescriptionItemId: String,
    var intakeDate: LocalDateTime?,
    var expectedAt: LocalDateTime?,
    var took: Boolean
)