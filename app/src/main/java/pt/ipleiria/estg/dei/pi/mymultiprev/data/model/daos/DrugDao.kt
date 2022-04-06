package pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug

@Dao
interface DrugDao {
    @Query("SELECT * FROM drugs ORDER BY id ASC")
    fun getDrugs(): Flow<List<Drug>>

    @Query("SELECT * FROM drugs WHERE id = :id")
    fun getDrugById(id: String): Flow<Drug>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrugs(drugs: List<Drug>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrug(drug: Drug)

    @Query("UPDATE drugs SET alias = :alias WHERE id = :id")
    fun setDrugAlias(id: String, alias: String)

    @Query("SELECT alias FROM drugs WHERE id = :id")
    suspend fun getDrugAlias(id: String): String?

    @Query("DELETE FROM drugs")
    fun deleteAll()
}