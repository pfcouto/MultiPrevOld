package pt.ipleiria.estg.dei.pi.mymultiprev.data.network

sealed class Resource<T>(
    val data: T? = null,
    val error: Throwable? = null,
    val isNetworkError: Boolean = false
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(throwable: Throwable?, data: T? = null, isNetworkError: Boolean = false) :
        Resource<T>(data, throwable, isNetworkError)
}
