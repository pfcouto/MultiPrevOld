package pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "drugs")
data class Drug(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    var name: String,
    var commercialName: String,
    var alias: String,
    var intakeMethod: String,
    var createAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    var pharmClass: String,
    var therapies: String,
    var dosageMassMg: Int,
    var dosageVolumeMl: Int
)
