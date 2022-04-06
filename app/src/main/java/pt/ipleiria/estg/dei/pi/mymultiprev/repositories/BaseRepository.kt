package pt.ipleiria.estg.dei.pi.mymultiprev.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import retrofit2.HttpException

interface BaseRepository {
    suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        Resource.Error(throwable, null, false)
                    }
                    else -> {
                        Resource.Error(throwable, null, true)
                    }
                }
            }
        }
    }

    suspend fun deleteData()
}