package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.prescriptionItemsHistory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.adapters.PrescriptionItemHistoryAdapter
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.databinding.PrescriptionItemsHistoryFragmentBinding
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.seeDetails.SeeDetailsViewModel

@AndroidEntryPoint
class PrescriptionItemsHistoryFragment : Fragment(),
    PrescriptionItemHistoryAdapter.PrescriptionItemHistoryClickListener {

    private val TAG = "PrescriptionItemsHistoryFragment"

    private val viewModel: PrescriptionItemsHistoryViewModel by viewModels()
    private lateinit var binding: PrescriptionItemsHistoryFragmentBinding
    private lateinit var prescriptionItemHistoryAdapter: PrescriptionItemHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PrescriptionItemsHistoryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i(TAG, "### Prescription Items History Created ###")
        setupRecyclerView()
        viewModel.prescriptionItems.observe(viewLifecycleOwner) { prescriptionItems ->
            when (prescriptionItems) {
                is Resource.Success -> {
                    Log.i(TAG, "Resource Success")
                    if (!prescriptionItems.data.isNullOrEmpty()) {
                        viewModel.updatePairs()
                    } else {
                        binding.medicineHistoryFlipper.displayedChild = 1
                    }
                }
                is Resource.Loading -> {
                    Log.i(TAG, "Resource Loading")
                }
                else -> {
                    Log.i(TAG, "Resource Error")
                }
            }
        }
        viewModel.drugs.observe(viewLifecycleOwner) { drugs ->
            if (!drugs.data.isNullOrEmpty()) {
                viewModel.updatePairs()
            }
        }

        changeAdapterData()

        binding.medicineSearch.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.i(TAG, "Inserted Text = $newText")
                viewModel.filterPairs(newText)
                return true
            }

        })
    }

    private fun changeAdapterData() {
        viewModel.pairs.observe(viewLifecycleOwner) { pairs ->
            Log.i(TAG, "Pairs changed $pairs")
            if (pairs.isNullOrEmpty()) {
                Log.i(TAG, "Pairs are NULL - Displaying No Prescription Items text")
                binding.medicineHistoryFlipper.displayedChild = 1
                return@observe
            }
            Log.i(TAG, "Pairs are NOT NULL - Displaying RecyclerView")
            prescriptionItemHistoryAdapter.updatePairs(pairs)
            binding.medicineHistoryFlipper.displayedChild = 2
        }
    }

    private fun setupRecyclerView() {
        Log.i(TAG, "Setting up RecyclerView")
        prescriptionItemHistoryAdapter = PrescriptionItemHistoryAdapter(listOf(), this)
        binding.recyclerViewMedicineHistory.adapter = prescriptionItemHistoryAdapter
        binding.recyclerViewMedicineHistory.layoutManager =
            LinearLayoutManager(requireContext())
        Log.i(TAG, "Finished Setting up RecyclerView")
    }

    override fun onSeeDetailsClick(pair: Pair<PrescriptionItem, Drug?>) {
        Log.i(TAG, "Clicked on item: ${pair.second!!.alias}")
        val seeDetailsViewModel: SeeDetailsViewModel by activityViewModels()
        seeDetailsViewModel.setPrescriptionItemDrugPair(pair)
        findNavController().navigate(
            R.id.action_medicineHistoryFragment_to_drugDetailsFragment,
            null,
        )
        Log.i(TAG, "Navigating to SeeDetailsFragment")
    }

    override fun onResume() {
        super.onResume()
        binding.medicineSearch.setQuery(viewModel.searchQuery, true)
    }
}