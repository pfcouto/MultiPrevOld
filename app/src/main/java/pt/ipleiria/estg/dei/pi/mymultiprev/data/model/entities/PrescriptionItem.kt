package pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Util

@Entity(tableName = "prescription_items")
data class PrescriptionItem(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    var drug: String,
    var intakeValue: Double,
    var frequency: Int,
    var pathology: String?,
    var intakeUnit: String,
    var acquiredAt: LocalDateTime?,
    var nextIntake: LocalDateTime?,
    var status: Int?,
    var expectedIntakeCount: Int?,
    var intakesTakenCount: Int? = 0,
    var prescription: String,
    var imageLocation: String?,
    var alarm: Boolean = true
) {
    val isOverdue: Boolean
        get() {
            val timeUntil = timeUntil()
            if (timeUntil != null) {
                return isNextDoseOverdue(timeUntil)
            }
            return false
        }

    val dosage: String
        get() = intakeValue.toString() + intakeUnit

    private fun isNextDoseOverdue(millis: Long) = millis <= 0

    fun timeUntil(): Long? {
        if (nextIntake != null) {
            return nextIntake!!.toInstant(Constants.TIME_ZONE)
                .toEpochMilliseconds() - Clock.System.now()
                .toEpochMilliseconds()
        }
        return null
    }

    fun formattedNextIntake() =
        Util.formatDateTime(nextIntake!!)
}
