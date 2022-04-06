package pt.ipleiria.estg.dei.pi.mymultiprev.ui.login

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import pt.ipleiria.estg.dei.pi.mymultiprev.crypto.SHA3Util
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Patient
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.requests.LoginRequest
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.AuthRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.SharedPreferencesRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.responses.LoginResponse
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sha3256: SHA3Util,
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val TAG = "LoginViewModel"

    private val sp = context.getSharedPreferences(Constants.AUTH_SP, Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() =
            sharedPreferencesRepository.isLoggedIn()
        set(value) = sp
            .edit()
            .putBoolean(Constants.FIRST_TIME_LOGIN, value).apply()

    private val _loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>>
        get() = _loginResponse

    var _patient: LiveData<Resource<Patient>> = MutableLiveData()
    val patient: LiveData<Resource<Patient>>
        get() = _patient


    fun login(username: String, password: String) {
        val hashPassword = digest(password)

        Log.i(TAG, "login() - $username / $hashPassword")

        val loginBody = LoginRequest(username, hashPassword)

        viewModelScope.launch {
            val loginResponseResource = authRepository.login(loginBody)
            _loginResponse.value = loginResponseResource
            Log.i(TAG, "loginResponseResource - $loginResponseResource")
        }
    }

    private fun digest(data: String) = sha3256.digest(data)

    fun savePatientId() {
        val patientId = _loginResponse.value!!.data!!.id
        val token = _loginResponse.value!!.data!!.token
        Log.i(TAG, "patientId - $patientId")
        viewModelScope.launch {
            sharedPreferencesRepository.saveToken(token)
            _patient = authRepository.getPatient(patientId).asLiveData()
            Log.i(TAG, "Patient Saved")
        }
        sp.edit().putString(Constants.PATIENT_ID, patientId).apply()
    }

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
}