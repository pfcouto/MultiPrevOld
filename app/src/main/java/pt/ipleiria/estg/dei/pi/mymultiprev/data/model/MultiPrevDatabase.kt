package pt.ipleiria.estg.dei.pi.mymultiprev.data.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos.*
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.*

@Database(
    entities = [PrescriptionItem::class, Drug::class, Patient::class, Intake::class, Alarm::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MultiPrevDatabase : RoomDatabase() {
    abstract fun prescriptionItemDao(): PrescriptionItemDao
    abstract fun drugDao(): DrugDao
    abstract fun authDao(): AuthDao
    abstract fun intakeDao(): IntakeDao
    abstract fun alarmDao(): AlarmDao
}