package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmNewIntake

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos.IntakeDTO
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.IntakeRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.SharedPreferencesRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import javax.inject.Inject

@HiltViewModel
class ConfirmIntakeViewModel @Inject constructor(
    private val intakeRepository: IntakeRepository,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : ViewModel() {

    private val TAG = "ConfirmIntakeViewModel"

    lateinit var prescriptionItem: PrescriptionItem
    lateinit var drug: Drug
    lateinit var scheduleIntakeDate: LocalDate

    private var _registrationIntakeDateTime: MutableLiveData<LocalDateTime> =
        MutableLiveData(Clock.System.now().toLocalDateTime(Constants.TIME_ZONE))
    val registrationIntakeDateTime: LiveData<LocalDateTime> get() = _registrationIntakeDateTime

    private var _response: MutableLiveData<Resource<IntakeDTO>> = MutableLiveData()
    val response: LiveData<Resource<IntakeDTO>>
        get() = _response


    fun setPrescriptionItemDrugPair(pair: Pair<PrescriptionItem, Drug?>) {
        prescriptionItem = pair.first
        if (pair.second != null) {
            drug = pair.second!!
        }
    }

    fun clearResponse() {
        Log.i(TAG, "Clearing Response")
        _response.value = null
        _registrationIntakeDateTime.value = Clock.System.now().toLocalDateTime(Constants.TIME_ZONE)
    }

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        Log.i(TAG, "Date = $dayOfMonth/$month/$year")
        scheduleIntakeDate = LocalDate(year, month + 1, dayOfMonth)
    }

    fun setTime(hourOfDay: Int, minute: Int) {
        Log.i(TAG, "Time = $hourOfDay/$minute")
        _registrationIntakeDateTime.value = LocalDateTime(
            scheduleIntakeDate.year,
            scheduleIntakeDate.monthNumber,
            scheduleIntakeDate.dayOfMonth,
            hourOfDay, minute
        )
    }

    fun verifyRegistrationDateTime(): Boolean {
        if (_registrationIntakeDateTime.value!! < prescriptionItem.nextIntake!!.toInstant(Constants.TIME_ZONE)
                .minus(2, DateTimeUnit.HOUR).toLocalDateTime(Constants.TIME_ZONE)
        ) {
            return false;
        }
        return true;
    }

    fun verifyRange(): Boolean {
        val first = prescriptionItem.nextIntake!!.toInstant(Constants.TIME_ZONE)
            .minus(2, DateTimeUnit.HOUR).toLocalDateTime(Constants.TIME_ZONE)
        val second = prescriptionItem.nextIntake!!.toInstant(Constants.TIME_ZONE)
            .plus(2, DateTimeUnit.HOUR).toLocalDateTime(Constants.TIME_ZONE)
        if (_registrationIntakeDateTime.value!! in first..second) {
            return true
        }
        return false
    }

    fun registerIntake() {
        viewModelScope.launch {
            _response.value = intakeRepository.doIntake(
                IntakeDTO(
                    id = null,
                    prescriptionItemId = prescriptionItem.id,
                    expectedAt = prescriptionItem.nextIntake.toString(),
                    patientId = sharedPreferencesRepository.getCurrentPatientId(),
                    intakeDate = registrationIntakeDateTime.value.toString(),
                    took = true
                )
            )
        }
    }

}