package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmAcquisition

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import pt.ipleiria.estg.dei.pi.mymultiprev.adapters.PrevisionDateAdapter
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.databinding.ConfirmAcquisitionFragmentBinding
import pt.ipleiria.estg.dei.pi.mymultiprev.util.LoadingDialog
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Util.handleError
import java.util.*


@AndroidEntryPoint
class ConfirmAcquisitionFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, NumberPicker.OnValueChangeListener {

    private val TAG = "ConfirmAcquisitionFragment"

    private val viewModel: ConfirmAcquisitionViewModel by activityViewModels()
    private lateinit var binding: ConfirmAcquisitionFragmentBinding
    private lateinit var adapter: PrevisionDateAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ConfirmAcquisitionFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.i(TAG, "### Confirm Acquisition Fragment Created ###")

        setupUI()
        setupRecyclerView()

        val loadingDialog = LoadingDialog(requireActivity())
        binding.buttonAgendar.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val datePickerDialog =
                DatePickerDialog(requireContext(), this, year, month, day)

            datePickerDialog.datePicker.apply {
                minDate = calendar.timeInMillis
                calendar.add(Calendar.WEEK_OF_YEAR, 2)
                maxDate = calendar.timeInMillis//+(1000*60*60*24*7)
            }
            datePickerDialog.setTitle(null)
            datePickerDialog.show()
        }

        binding.apply {
            buttonCancel1.setOnClickListener {
                findNavController().popBackStack()
            }

            buttonConfirm.setOnClickListener {
                loadingDialog.startLoadingDialog()
                viewModel.confirmAcquisition(
                    textInputLayout.editText?.text.toString(),
                    numberPickerFrequency.value
                )
            }
        }

        viewModel.apply {
            predictDates.observe(viewLifecycleOwner) {
                Log.i(TAG, it.toString())
                adapter = PrevisionDateAdapter(it)
                binding.recyclerViewPrevisionDate.layoutManager =
                    LinearLayoutManager(requireActivity())
                binding.recyclerViewPrevisionDate.adapter = adapter
            }

            response.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                        Log.i(TAG, "Resource Success")
                        loadingDialog.dismissDialog()
                        viewModel.clearResponse()
                        findNavController().popBackStack()
                    }
                    is Resource.Loading -> {
                        Log.i(TAG, "Resource Loading")
                    }
                    is Resource.Error -> {
                        Log.i(TAG, "Resource Error")
                        loadingDialog.dismissDialog()
                        handleError(it)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearResponse()
    }

    override fun onDetach() {
        viewModel.clearResponse()
        super.onDetach()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.setDate(year, month, dayOfMonth)
        val c = Calendar.getInstance()
        val hour = c[Calendar.HOUR_OF_DAY]
        val minute = c[Calendar.MINUTE]
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            this,
            hour,
            minute,
            DateFormat.is24HourFormat(requireContext())
        )
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        viewModel.setTime(hourOfDay, minute)
        binding.buttonConfirm.isEnabled = true
        viewModel.recalculatePredictionDates(binding.numberPickerFrequency.value)
    }

    private fun setupUI() {
        binding.apply {
            prescriptionItem = viewModel.prescriptionItem
            drug = viewModel.drug
            numberPickerFrequency.apply {
                maxValue = viewModel.prescriptionItem.frequency + 2
                minValue = viewModel.prescriptionItem.frequency - 2
                wrapSelectorWheel = false
                value = viewModel.prescriptionItem.frequency
                setOnValueChangedListener(this@ConfirmAcquisitionFragment)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = PrevisionDateAdapter(listOf())
        binding.recyclerViewPrevisionDate.adapter = adapter
        binding.recyclerViewPrevisionDate.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
        viewModel.recalculatePredictionDates(newVal)
    }
}