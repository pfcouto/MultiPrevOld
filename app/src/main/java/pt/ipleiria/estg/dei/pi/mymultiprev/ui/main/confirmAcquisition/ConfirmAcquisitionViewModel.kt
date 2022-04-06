package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmAcquisition

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
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos.PrescriptionItemDTO
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.PrescriptionItemsRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ConfirmAcquisitionViewModel @Inject constructor(
    private val prescriptionItemsRepository: PrescriptionItemsRepository
) : ViewModel() {

    private val TAG = "ConfirmAcquisitionViewModel"

    lateinit var prescriptionItem: PrescriptionItem
    lateinit var drug: Drug
    lateinit var scheduleIntakeDate: LocalDate
    private lateinit var scheduleIntakeDateTime: LocalDateTime

    private var _response: MutableLiveData<Resource<PrescriptionItemDTO>> = MutableLiveData()
    val response: LiveData<Resource<PrescriptionItemDTO>>
        get() = _response

    private var _predictDates: MutableLiveData<List<LocalDateTime>> = MutableLiveData(listOf())
    val predictDates: LiveData<List<LocalDateTime>>
        get() = _predictDates

    fun setPrescriptionItemDrugPair(pair: Pair<PrescriptionItem, Drug?>) {
        prescriptionItem = pair.first
        if (pair.second != null) {
            drug = pair.second!!
        }
    }

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        Log.i(TAG, "Date = $dayOfMonth/$month/$year")
        scheduleIntakeDate = LocalDate(year, month + 1, dayOfMonth)
    }

    fun setTime(hourOfDay: Int, minute: Int) {
        Log.i(TAG, "Time = $hourOfDay/$minute")
        scheduleIntakeDateTime = LocalDateTime(
            scheduleIntakeDate.year,
            scheduleIntakeDate.monthNumber,
            scheduleIntakeDate.dayOfMonth,
            hourOfDay, minute
        )
    }

    fun clearResponse() {
        Log.i(TAG, "Clearing Response")
        _response = MutableLiveData()
        _predictDates = MutableLiveData()
    }

    fun recalculatePredictionDates(frequency: Int) {
        if (!this::scheduleIntakeDateTime.isInitialized)
            return

        var aux = scheduleIntakeDateTime.toInstant(Constants.TIME_ZONE)
        val predictionDates = ArrayList<LocalDateTime>()
        val numberOfPredictions = Constants.HOURS_OF_DAY / frequency

        predictionDates.add(aux.toLocalDateTime(Constants.TIME_ZONE))
        for (i in 1..numberOfPredictions) {
            aux = aux.plus(frequency, DateTimeUnit.HOUR)
            predictionDates.add(aux.toLocalDateTime(Constants.TIME_ZONE))
        }
        _predictDates.value = predictionDates
    }

    fun confirmAcquisition(pathology: String?, frequency: Int) {
        val updatedPrescriptionItem = prescriptionItem
        updatedPrescriptionItem.pathology = pathology
        updatedPrescriptionItem.nextIntake = scheduleIntakeDateTime
        updatedPrescriptionItem.acquiredAt =
            Clock.System.now().toLocalDateTime(Constants.TIME_ZONE)
        updatedPrescriptionItem.frequency = frequency
        viewModelScope.launch {
            _response.value = prescriptionItemsRepository.updatePrescription(
                prescriptionItem.id,
                updatedPrescriptionItem
            )
        }
    }
}