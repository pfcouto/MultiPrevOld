package pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Patient

@Dao
interface AuthDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setPatient(patient: Patient)

    @Query("SELECT * FROM patients WHERE id = :id")
    fun getPatient(id: String): Flow<Patient>

    @Query("DELETE FROM patients")
    fun deleteAll()
}