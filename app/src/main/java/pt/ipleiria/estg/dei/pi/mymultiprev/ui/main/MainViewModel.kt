package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val authRepository: AuthRepository,
    private val prescriptionItemsRepository: PrescriptionItemsRepository,
    private val intakeRepository: IntakeRepository,
    private val drugRepository: DrugRepository,
) : ViewModel() {

    private var _isAllClear: MutableLiveData<Boolean> = MutableLiveData(false)
    val isAllClear: LiveData<Boolean>
        get() = _isAllClear

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

    private fun deleteSharedPreferences() {
        sharedPreferencesRepository.deleteAll()
    }

    fun deleteAppData() {
        viewModelScope.launch {
            drugRepository.deleteData()
            authRepository.deleteData()
            intakeRepository.deleteData()
            prescriptionItemsRepository.deleteData()
            deleteSharedPreferences()
            _isAllClear.value = true
        }
    }
}