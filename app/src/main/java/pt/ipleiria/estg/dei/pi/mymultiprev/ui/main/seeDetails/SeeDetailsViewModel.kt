package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.seeDetails

import android.net.Uri
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Intake
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.DrugRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.IntakeRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.PrescriptionItemsRepository
import javax.inject.Inject

@HiltViewModel
class SeeDetailsViewModel @Inject constructor(
    private val prescriptionItemsRepository: PrescriptionItemsRepository,
    private val drugRepository: DrugRepository,
    private val intakeRepository: IntakeRepository
) :
    ViewModel() {

    private val TAG = "SeeDetailsViewModel"

    private var _photoUri: MutableLiveData<Uri?> = MutableLiveData()
    val photoUri: LiveData<Uri?>
        get() = _photoUri

    private var _prescriptionItem: MutableLiveData<PrescriptionItem> = MutableLiveData()
    val prescriptionItem: LiveData<PrescriptionItem>
        get() = _prescriptionItem

    private var _drug: MutableLiveData<Drug> = MutableLiveData()
    val drug: LiveData<Drug>
        get() = _drug

    lateinit var intakes: LiveData<Resource<List<Intake>>>

    fun getPrescriptionItemPhoto(id: String) {
        viewModelScope.launch {
            _photoUri.value = prescriptionItemsRepository.getPrescriptionItemPhoto(id)
        }
    }

    fun setPrescriptionItemDrugPair(pair: Pair<PrescriptionItem, Drug?>) {
        _prescriptionItem.value = pair.first
        _drug.value = pair.second
    }

    fun getIntakes() {
        intakes = intakeRepository.getIntakesByPrescriptionItemId(_prescriptionItem.value!!.id)
            .asLiveData()
    }

    fun setPrescriptionItemAlias(id: String, alias: String) {
        _drug.value?.alias = alias
        viewModelScope.launch {
            drugRepository.setDrugAlias(id, alias)
        }
    }
}