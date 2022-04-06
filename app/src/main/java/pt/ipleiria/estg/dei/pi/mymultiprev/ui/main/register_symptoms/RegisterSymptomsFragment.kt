package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.register_symptoms

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.quickbirdstudios.surveykit.*
import com.quickbirdstudios.surveykit.backend.views.main_parts.AbortDialogConfiguration
import com.quickbirdstudios.surveykit.result.question_results.MultipleChoiceQuestionResult
import com.quickbirdstudios.surveykit.result.question_results.SingleChoiceQuestionResult
import com.quickbirdstudios.surveykit.steps.CompletionStep
import com.quickbirdstudios.surveykit.steps.InstructionStep
import com.quickbirdstudios.surveykit.steps.QuestionStep
import com.quickbirdstudios.surveykit.steps.Step
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Symptom
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.enums.EvolutionChoices
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.enums.SymptomRegistrationSituation
import pt.ipleiria.estg.dei.pi.mymultiprev.databinding.RegisterSymptomsFragmentBinding
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import java.time.LocalDate
import java.time.Period

@AndroidEntryPoint
class RegisterSymptomsFragment : Fragment() {

    private val TAG = "RegisterSymptomsFragment"

    private val viewModel: RegisterSymptomsViewModel by activityViewModels()
    private lateinit var binding: RegisterSymptomsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RegisterSymptomsFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "### Register Symptoms Fragment Created ###")

        viewModel.apply {
            getSymptomTypeItems()
            getPatient()

            symptomTypesListResponse.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    val symptomTypesChoices = arrayListOf<TextChoice>()
                    it.forEach { symptomTypeItem ->
                        symptomTypesChoices.add(
                            TextChoice(
                                symptomTypeItem.name,
                                symptomTypeItem.id
                            )
                        )
                    }

                    val initialStep = createInitialStep()

                    val step1 = QuestionStep(
                        title = getString(R.string.step1_title),
                        text = getString(R.string.step1_desc),
                        answerFormat = AnswerFormat.MultipleChoiceAnswerFormat(
                            symptomTypesChoices
                        ),
                        nextButtonText = getString(R.string.step_next)
                    )

                    val evolutionChoices = arrayListOf(
                        TextChoice(
                            getString(R.string.in_recuperation),
                            EvolutionChoices.Recovering.name
                        ),
                        TextChoice(getString(R.string.cured), EvolutionChoices.Cure.name)
                    )

                    val step2 = QuestionStep(
                        title = getString(R.string.step2_title),
                        text = getString(R.string.step2_desc),
                        answerFormat = AnswerFormat.SingleChoiceAnswerFormat(
                            evolutionChoices
                        ),
                        nextButtonText = getString(R.string.step_next)
                    )

                    if (specificPrescriptionItemId.isEmpty()) {
                        Log.i(TAG, "Registering throughout the day")
                        prescriptionItems.observe(viewLifecycleOwner) { prescriptionItems ->
                            if (!prescriptionItems.data.isNullOrEmpty() && drugs.value?.data != null && !isFetched) {
                                createCompleteForm(initialStep, step1, step2)
                                isFetched = true
                            }
                        }

                        drugs.observe(viewLifecycleOwner) { drugs ->
                            if (!drugs.data.isNullOrEmpty() && prescriptionItems.value?.data != null && !isFetched) {
                                createCompleteForm(initialStep, step1, step2)
                                isFetched = true
                            }
                        }
                    } else {
                        Log.i(TAG, "Registering during intake - $specificPrescriptionItemId")
                        getSpecificPrescriptionItem()
                        specificPrescriptionItem.observe(viewLifecycleOwner) { prescriptionItem ->
                            if (prescriptionItem != null && !isFetched) {
                                createCompleteForm(initialStep, step1, step2)
                                isFetched = true
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearData()
    }
    override fun onDetach() {
        super.onDetach()
        viewModel.clearData()
    }

    private fun createCompleteForm(initialStep: Step, step1: Step, step2: Step) {
        val prescriptionItemsChoices = arrayListOf<TextChoice>()
        if (viewModel.specificPrescriptionItemId.isEmpty()) {
            viewModel.prescriptionItems.value!!.data!!.forEach { prescriptionItem ->
                prescriptionItemsChoices.add(
                    TextChoice(
                        if (viewModel.findDrugById(prescriptionItem.drug)!!.alias.isEmpty()) {
                            viewModel.findDrugById(prescriptionItem.drug)!!.commercialName
                        } else {
                            viewModel.findDrugById(prescriptionItem.drug)!!.alias
                        },
                        viewModel.findDrugById(prescriptionItem.drug)!!.id
                    )
                )
            }


        }
        val step3 = QuestionStep(
            title = getString(R.string.step3_title),
            text = getString(R.string.step3_desc),
            answerFormat = AnswerFormat.SingleChoiceAnswerFormat(
                prescriptionItemsChoices
            ),
            nextButtonText = getString(R.string.step_next)
        )

        val step4 = QuestionStep(
            title = getString(R.string.step4_title),
            text = getString(R.string.step4_desc),
            answerFormat = AnswerFormat.IntegerAnswerFormat(
                hint = getString(R.string.step4_hint),
                defaultValue = Period.between(
                    viewModel.patient.value!!.birthdate.date.toJavaLocalDate(),
                    LocalDate.now()
                ).years
            ),
            nextButtonText = getString(R.string.step_next)
        )

        val contactedDoctorChoices = arrayListOf(
            TextChoice(getString(R.string.yes), true.toString()),
            TextChoice(getString(R.string.no), false.toString())
        )
        val step5 = QuestionStep(
            title = getString(R.string.step5_title),
            text = getString(R.string.step5_desc),
            answerFormat = AnswerFormat.SingleChoiceAnswerFormat(
                contactedDoctorChoices,
                contactedDoctorChoices[0]
            ),
            nextButtonText = getString(R.string.step_next)
        )

        val finalStep = createFinalStep()

        val steps = if (viewModel.specificPrescriptionItemId.isEmpty()) {
            listOf(initialStep, step1, step2, step3, step4, step5, finalStep)
        } else {
            listOf(initialStep, step1, step2, step4, step5, finalStep)
        }

        val task = OrderedTask(steps = steps)

        val configuration = createFormTheme()

        binding.surveyView.start(task, configuration)

        binding.surveyView.onSurveyFinish = { taskResult, finishReason ->
            if (finishReason == FinishReason.Completed) {
                val symptoms = mutableListOf<Symptom>()
                val typeId: MutableList<String?> = mutableListOf()
                var drugId: String? = null
                val registrationSituation =
                    if (viewModel.specificPrescriptionItemId.isEmpty()) SymptomRegistrationSituation.ThroughOutTheDay else SymptomRegistrationSituation.DuringIntake
                val patientId = viewModel.patientId
                taskResult.results.forEach { stepResult ->
                    if (stepResult.id == step1.id) {
                        (stepResult.results.firstOrNull() as MultipleChoiceQuestionResult).answer.forEach {
                            typeId.add(it.value)
                        }
                    }
                    if (stepResult.id == step3.id) {
                        drugId =
                            (stepResult.results.firstOrNull() as SingleChoiceQuestionResult).answer!!.value
                    }
                }
                val drugIdToSend = if (viewModel.specificPrescriptionItemId.isEmpty()) {
                    drugId!!
                } else {
                    viewModel.specificPrescriptionItem.value!!.drug
                }
                if (typeId.isNotEmpty() && drugId != null) {
                    typeId.forEachIndexed { index, it ->
                        symptoms.add(
                            Symptom(
                                patientId = patientId,
                                typeId = it!!,
                                registratedSituation = registrationSituation,
                                drugId = drugIdToSend,
                                registeredAt = Clock.System.now()
                                    .toLocalDateTime(Constants.TIME_ZONE).toString()
                            )
                        )
                    }
                }
                viewModel.addSymptomsToRegister(symptoms)
                viewModel.registerSymptoms()
                viewModel.clearData()
                findNavController().navigate(R.id.action_registerSymptomsFragment_to_activeDrugListFragment)
            }
        }
    }

    private fun createFormTheme(): SurveyTheme = SurveyTheme(
        themeColorDark = ResourcesCompat.getColor(
            resources,
            R.color.colorGreen,
            requireActivity().theme
        ),
        themeColor = ResourcesCompat.getColor(
            resources,
            R.color.colorGreen,
            requireActivity().theme
        ),
        textColor = ResourcesCompat.getColor(
            resources,
            R.color.colorGreen,
            requireActivity().theme
        ),
        abortDialogConfiguration = AbortDialogConfiguration(
            title = R.string.cancel,
            message = R.string.cancel_confirmation,
            neutralMessage = R.string.no,
            negativeMessage = R.string.yes
        )
    )

    private fun createInitialStep(): Step = InstructionStep(
        title = getString(R.string.introduction_title),
        text = getString(R.string.introduction_desc),
        buttonText = getString(R.string.introduction_button)
    )

    private fun createFinalStep(): Step = CompletionStep(
        title = getString(R.string.completion_title),
        lottieAnimation = CompletionStep.LottieAnimation.RawResource(R.raw.check_lottie_anim),
        text = getString(R.string.completion_desc),
        buttonText = getString(R.string.completion_button)
    )

}
