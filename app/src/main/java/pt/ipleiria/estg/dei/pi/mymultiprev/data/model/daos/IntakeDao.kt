package pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Intake

@Dao
interface IntakeDao {
    @Query("SELECT * FROM intakes WHERE prescriptionItemId = :pId ORDER BY expectedAt ASC")
    fun getIntakesByPrescriptionItemId(pId: String): Flow<List<Intake>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntakes(intakes: List<Intake>)

    @Query("DELETE FROM intakes")
    fun deleteAll()
}