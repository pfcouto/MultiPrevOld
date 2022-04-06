package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.seeDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import pt.ipleiria.estg.dei.pi.mymultiprev.adapters.IntakeAdapter
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.enums.PrescriptionItemStatus
import pt.ipleiria.estg.dei.pi.mymultiprev.databinding.FragmentIntakeHistoryBinding

class IntakeHistoryFragment : Fragment() {

    private lateinit var binding: FragmentIntakeHistoryBinding
    private val viewModel: SeeDetailsViewModel by activityViewModels()
    private lateinit var adapter: IntakeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntakeHistoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.apply {
            getIntakes()
            intakes.observe(viewLifecycleOwner) { intakeListResource ->
                val intakeList = intakeListResource.data as ArrayList
                if (intakeList.size > 0) {
                    if (prescriptionItem.value!!.status == PrescriptionItemStatus.InProgress.value()) {
                        intakeList.removeLast()
                    }
                    binding.apply {
                        if (intakeList.isNotEmpty()) {
                            labelNoIntakes.visibility = View.GONE
                            recyclerViewIntakes.visibility = View.VISIBLE
                            adapter = IntakeAdapter(intakeList)
                            recyclerViewIntakes.layoutManager =
                                LinearLayoutManager(requireActivity())
                            recyclerViewIntakes.adapter = adapter
                        } else {
                            labelNoIntakes.visibility = View.VISIBLE
                            recyclerViewIntakes.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}