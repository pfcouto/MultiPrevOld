package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmNewIntake

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.datetime.toInstant
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.databinding.FragmentNewIntakeDetailsBinding
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.register_symptoms.RegisterSymptomsViewModel
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import pt.ipleiria.estg.dei.pi.mymultiprev.util.LoadingDialog
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Util
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Util.handleError
import java.util.*


@AndroidEntryPoint
class ConfirmIntakeDetailsFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private val TAG = "CreateNewIntakeFragment"

    private val viewModel: ConfirmIntakeViewModel by activityViewModels()
    private lateinit var binding: FragmentNewIntakeDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewIntakeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "### Created New Intake Fragment ###")

        val loadingDialog = LoadingDialog(requireActivity())
        setupUI()
        setupDateTimePicker()
        viewModel.apply {
            registrationIntakeDateTime.observe(viewLifecycleOwner) { registrationDate ->
                Log.i(TAG, "Registration Date = $registrationDate")
                binding.textViewRegistrationDateTime.text = Util.formatDateTime(registrationDate)
                binding.buttonNext.isEnabled = verifyRegistrationDateTime()
                if (!binding.buttonNext.isEnabled) {
                    displayError()
                } else {
                    hideError()
                }
                displayStatus()
            }

            response.observe(viewLifecycleOwner) { response ->
                if (response == null) return@observe
                when (response) {
                    is Resource.Success -> {
                        loadingDialog.dismissDialog()
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle(getString(R.string.symptoms_title))
                        builder.setMessage(getString(R.string.felt_symptoms_dialog_message))

                        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
                            clearResponse()
                            dialog.dismiss()
                            val registerSymptomsViewModel: RegisterSymptomsViewModel by activityViewModels()
                            registerSymptomsViewModel.specificPrescriptionItemId =
                                response.data!!.prescriptionItemId
                            requireActivity().findNavController(R.id.nav_host_fragment)
                                .navigate(R.id.action_newIntakeDetailsFragment_to_registerSymptomsFragment)
                        }
                        builder.setNegativeButton(
                            getString(R.string.no)
                        ) { dialog, which ->
                            clearResponse()
                            dialog.dismiss()
                            requireActivity().onBackPressed()
                        }

                        val alert = builder.create()
                        alert.show()
                    }
                    is Resource.Error -> {
                        loadingDialog.dismissDialog()
                        handleError(response)
                    }
                    else -> {
                        loadingDialog.dismissDialog()
                    }
                }
            }
        }

        binding.apply {
            buttonCancel.setOnClickListener {
                requireActivity().onBackPressed()
            }

            buttonNext.setOnClickListener {
                loadingDialog.startLoadingDialog()
                viewModel.registerIntake()
            }
        }
    }

    private fun setupUI() {
        binding.apply {
            prescriptionItem = viewModel.prescriptionItem
            drug = viewModel.drug
            textViewRegistrationDateTime.text =
                Util.formatDateTime(viewModel.registrationIntakeDateTime.value!!)
            if (viewModel.prescriptionItem.expectedIntakeCount == viewModel.prescriptionItem.intakesTakenCount!! + 1) {
                textViewLastIntake.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearResponse()
    }

    private fun setupDateTimePicker() {
        binding.buttonEditDateTime.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val datePickerDialog =
                DatePickerDialog(requireContext(), this, year, month, day)
            datePickerDialog.datePicker.apply {
                minDate = viewModel.prescriptionItem.nextIntake!!.toInstant(
                    Constants.TIME_ZONE
                ).toEpochMilliseconds()
                maxDate = System.currentTimeMillis()
            }
            datePickerDialog.setTitle(null)
            datePickerDialog.show()
        }
    }

    private fun displayError() {
        binding.textViewErrors.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.textViewErrors.visibility = View.GONE
    }

    private fun displayStatus() {
        if (viewModel.verifyRange()) {
            Log.i(TAG, "Dentro da recomendação")
            binding.textViewState.text = getString(R.string.intake_state_recomended)
            binding.textViewState.setTextColor(requireContext().getColor(R.color.colorGreen))
        } else {
            Log.i(TAG, "Fora da recomendação")
            binding.textViewState.text = getString(R.string.intake_state_non_recomended)
            binding.textViewState.setTextColor(requireContext().getColor(R.color.colorRed))
        }
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
    }
}