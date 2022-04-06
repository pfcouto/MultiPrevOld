package pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "alarms")
data class Alarm(@PrimaryKey(autoGenerate = true) var id: Int, var alarm: LocalDateTime)
