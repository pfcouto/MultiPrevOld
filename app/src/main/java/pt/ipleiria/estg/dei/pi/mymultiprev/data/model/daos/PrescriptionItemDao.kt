package pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem

@Dao
interface PrescriptionItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescriptionItem(prescriptionItem: PrescriptionItem)

    @Query("SELECT * FROM prescription_items WHERE status = 0 OR status = 1 ORDER BY id ASC")
    fun getPrescriptionItems(): Flow<List<PrescriptionItem>>

    @Query("SELECT * FROM prescription_items WHERE status = 2 ORDER BY id ASC")
    fun getCompletedPrescriptionItems(): Flow<List<PrescriptionItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescriptionItems(prescriptionItems: List<PrescriptionItem>)

    @Query("UPDATE prescription_items SET nextIntake = :nextIntakeUpdated WHERE id = :id")
    fun updateNextIntake(id: String, nextIntakeUpdated: LocalDateTime)

    @Query("UPDATE prescription_items SET status = :status,intakesTakenCount=:intakeCount WHERE id = :id")
    fun updateStatus(id: String, status: Int, intakeCount: Int)

    @Update
    suspend fun updatePrescriptionItem(prescriptionItem: PrescriptionItem)

    @Query("SELECT * FROM prescription_items WHERE id = :id")
    suspend fun getPrescriptionItemById(id: String): PrescriptionItem

    @Query("UPDATE prescription_items SET imageLocation = :photo_uri WHERE id = :id")
    fun setPrescriptionItemPhoto(id: String, photo_uri: String)

    @Query("SELECT imageLocation FROM prescription_items WHERE id = :id")
    suspend fun getPrescriptionItemPhoto(id: String): String?

    @Query("SELECT alarm FROM prescription_items WHERE id = :id")
    suspend fun getPrescriptionItemAlarm(id: String): Boolean?

    @Query("DELETE FROM prescription_items")
    fun deleteAll()
}