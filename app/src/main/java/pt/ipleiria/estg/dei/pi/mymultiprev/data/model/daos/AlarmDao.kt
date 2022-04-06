package pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.datetime.LocalDateTime
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Alarm

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarms ORDER BY alarm")
    suspend fun getAlarms(): List<Alarm>

    @Query("DELETE FROM alarms WHERE alarm < :currentTime")
    suspend fun deleteExpiredAlarms(currentTime: LocalDateTime)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAlarm(alarm: Alarm)

    @Query("UPDATE prescription_items SET alarm = :alarmState WHERE id = :prescriptionItemId")
    suspend fun setAlarmState(alarmState: Boolean, prescriptionItemId: String)

    @Query("DELETE FROM alarms")
    suspend fun deleteAll()
}