package pt.ipleiria.estg.dei.pi.mymultiprev.data.network

import kotlinx.coroutines.flow.*
import retrofit2.HttpException

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {
    val data = query().first()
    val flow = if (shouldFetch(data)) {
        emit(Resource.Loading(data))
        try {
            saveFetchResult(fetch())
            query().map { Resource.Success(it) }
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> query().map { Resource.Error(throwable, it, false) }
                else -> query().map { Resource.Error(throwable, it, true) }
            }

        }
    } else {
        query().map { Resource.Success(it) }
    }
    emitAll(flow)
}