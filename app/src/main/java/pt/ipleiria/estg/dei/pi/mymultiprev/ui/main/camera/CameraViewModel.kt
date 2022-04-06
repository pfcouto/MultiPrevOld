package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.camera

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.PrescriptionItemsRepository
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val prescriptionItemsRepository: PrescriptionItemsRepository
) : ViewModel() {

    private var _isPhotoSaved: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPhotoSaved: LiveData<Boolean>
        get() = _isPhotoSaved

    private var _prescriptionItemId: MutableLiveData<String> = MutableLiveData()
    val prescriptionItemId: LiveData<String>
        get() = _prescriptionItemId

    fun setPrescriptionItemPhoto(id: String, photo_uri: Uri) {
        viewModelScope.launch {
            prescriptionItemsRepository.setPrescriptionItemPhoto(id, photo_uri)
            _isPhotoSaved.value = true
        }
    }

    fun setPrescriptionItemId(id: String) = run { _prescriptionItemId.value = id }

}