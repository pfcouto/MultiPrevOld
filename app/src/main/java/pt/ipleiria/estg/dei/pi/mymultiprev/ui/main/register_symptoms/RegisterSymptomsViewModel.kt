package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.register_symptoms

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Patient
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Symptom
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.ServiceBuilder
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.services.SymptomsService
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.AuthRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.DrugRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.PrescriptionItemsRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.SharedPreferencesRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.responses.SymptomTypeItemResponse
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import javax.inject.Inject

@HiltViewModel
class RegisterSymptomsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val prescriptionItemsRepository: PrescriptionItemsRepository,
    drugRepository: DrugRepository,
    private val authRepository: AuthRepository,
    sharedPreferencesRepository: SharedPreferencesRepository
) :
    ViewModel() {

    private val TAG = "RegisterSymptomsViewModel"

    private val sp = context.getSharedPreferences(Constants.AUTH_SP, Context.MODE_PRIVATE)
    val patientId
        get() = sp.getString(Constants.PATIENT_ID, "")!!

    private var _symptomTypesListResponse: MutableLiveData<List<SymptomTypeItemResponse>> =
        MutableLiveData(listOf())
    val symptomTypesListResponse: LiveData<List<SymptomTypeItemResponse>>
        get() = _symptomTypesListResponse

    private val multiPrevServices =
        ServiceBuilder(sharedPreferencesRepository).buildService(SymptomsService::class.java)

    fun getSymptomTypeItems() {
        viewModelScope.launch {
            _symptomTypesListResponse.value = multiPrevServices.getAllSymptomTypes()
        }
    }

    var specificPrescriptionItemId: String = ""
    private var _specificPrescriptionItem: MutableLiveData<PrescriptionItem> = MutableLiveData()
    val specificPrescriptionItem: LiveData<PrescriptionItem>
        get() = _specificPrescriptionItem

    private var _patient: MutableLiveData<Patient> = MutableLiveData()
    val patient: LiveData<Patient>
        get() = _patient

    fun getPatient() {
        viewModelScope.launch {
            _patient.value = authRepository.getPatient(patientId).first().data
        }
    }

    val drugs = drugRepository.getDrugs(patientId).asLiveData()
    val prescriptionItems =
        prescriptionItemsRepository.getActivePrescriptionItems(patientId)
            .asLiveData()

    var isFetched = false

    fun findDrugById(id: String): Drug? {
        val drugs = this.drugs.value?.data
        if (!drugs.isNullOrEmpty()) {
            drugs.forEach { drug ->
                if (drug.id == id) {
                    return drug
                }
            }
        }
        return null
    }


    private var _symptomsToRegister: MutableLiveData<List<Symptom>> =
        MutableLiveData(listOf())
    val symptomsToRegister: LiveData<List<Symptom>>
        get() = _symptomsToRegister

    fun addSymptomsToRegister(symptomRequestList: List<Symptom>) {
        _symptomsToRegister.value = symptomRequestList
    }

    fun registerSymptoms() {
        viewModelScope.launch {
            multiPrevServices.registerSymptoms(symptomsToRegister.value!!)
        }
    }

    fun clearData() {
        Log.i(TAG, "Clearing Data")
        _symptomsToRegister = MutableLiveData(listOf())
        _symptomTypesListResponse = MutableLiveData(listOf())
        isFetched = false
        _specificPrescriptionItem = MutableLiveData()
        specificPrescriptionItemId = ""
    }

    fun getSpecificPrescriptionItem() {
        viewModelScope.launch {
            _specificPrescriptionItem.value =
                prescriptionItemsRepository.getLocalPrescriptionItem(specificPrescriptionItemId)
        }
    }

}