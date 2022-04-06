package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.prescriptionItemsHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.DrugRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.PrescriptionItemsRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.SharedPreferencesRepository
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PrescriptionItemsHistoryViewModel @Inject constructor(
    prescriptionItemsRepository: PrescriptionItemsRepository,
    drugRepository: DrugRepository,
    sharedPreferencesRepository: SharedPreferencesRepository,
) : ViewModel() {

    private val TAG = "PrescriptionItemHistoryViewModel"

    var searchQuery: String = ""

    private val patientId = sharedPreferencesRepository.getCurrentPatientId()

    val drugs = drugRepository.getDrugs(patientId)
        .asLiveData()

    val prescriptionItems =
        prescriptionItemsRepository.getCompletedPrescriptionItems(patientId)
            .asLiveData()

    private var _allPairs: MutableLiveData<List<Pair<PrescriptionItem, Drug?>>> = MutableLiveData(
        listOf()
    )

    private var _pairs: MutableLiveData<List<Pair<PrescriptionItem, Drug?>>> = MutableLiveData(
        listOf()
    )
    val pairs: LiveData<List<Pair<PrescriptionItem, Drug?>>>
        get() = _pairs


    fun updatePairs() {
        val prescriptions = prescriptionItems.value?.data
        val list: MutableList<Pair<PrescriptionItem, Drug?>> = mutableListOf()
        if (!prescriptions.isNullOrEmpty()) {
            prescriptions.forEach { prescription ->
                val drug = getDrugById(prescription.drug)
                list.add(prescription to drug)
            }
            _allPairs.value = Collections.unmodifiableList(list)
            _pairs.value = _allPairs.value
        }
    }

    private fun getDrugById(id: String): Drug? {
        if (drugs.value == null || drugs.value?.data.isNullOrEmpty()) {
            return null
        }
        return drugs.value?.data!!.find { drug -> drug.id == id }
    }

    fun filterPairs(newText: String?) {
        searchQuery = newText ?: ""
        if (searchQuery.isNotBlank()) {
            searchQuery = searchQuery.toLowerCase(Locale.getDefault())
            _pairs.value = _allPairs.value?.filter { pair ->
                containsString(pair)
            }
        } else
            _pairs.value = _allPairs.value
    }

    private fun containsString(pair: Pair<PrescriptionItem, Drug?>): Boolean {
        val alias = pair.second?.alias?.toLowerCase(Locale.getDefault())
        val commercialName = pair.second?.commercialName?.toLowerCase(Locale.getDefault())
        return alias!!.contains(searchQuery) || commercialName!!.contains(searchQuery)
    }

}