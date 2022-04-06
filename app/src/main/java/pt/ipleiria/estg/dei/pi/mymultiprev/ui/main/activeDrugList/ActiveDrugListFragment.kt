package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.activeDrugList


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.adapters.ActivePrescriptionAdapter
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.databinding.ActiveDrugListFragmentBinding
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmAcquisition.ConfirmAcquisitionViewModel
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmNewIntake.ConfirmIntakeViewModel
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.seeDetails.SeeDetailsViewModel
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants

@AndroidEntryPoint
class ActiveDrugListFragment : Fragment(),
    ActivePrescriptionAdapter.ActivePrescriptionClickListener {

    private val TAG = "ActiveDrugListFragment"

    private val viewModel: ActiveDrugListViewModel by activityViewModels()
    private val confirmViewModel: ConfirmAcquisitionViewModel by activityViewModels()
    private val confirmIntakeViewModel: ConfirmIntakeViewModel by activityViewModels()
    private val seeDetailsViewModel: SeeDetailsViewModel by activityViewModels()

    private lateinit var binding: ActiveDrugListFragmentBinding
    private lateinit var adapter: ActivePrescriptionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.active_drug_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "### Active Drug List Created ###")
        activity?.findViewById<TextView>(R.id.textViewFragmentTitle)?.visibility = View.VISIBLE
        binding = ActiveDrugListFragmentBinding.bind(view)
    }

    override fun onResume() {
        super.onResume()
        binding.switchLayout.isChecked = !viewModel.layoutPreference.value!!.value()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.layoutPreference.observe(viewLifecycleOwner) {
            Log.i(TAG, "Layout Preference - $it")
            if (it.value()) {
                binding.activePrescriptionItemsLayoutFlipper.displayedChild = 0

            } else {
                binding.activePrescriptionItemsLayoutFlipper.displayedChild = 1
            }
        }

        binding.switchLayout.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setLayoutPreference(!isChecked)
            viewModel.saveLayoutPreference(!binding.switchLayout.isChecked)
            Log.i(TAG, "Saved Layout Preference")
        }

        viewModel.prescriptionItems.observe(viewLifecycleOwner) { prescriptionItems ->
            when (prescriptionItems) {
                is Resource.Success -> {
                    Log.d(TAG, "Resource Success")
                    if (!prescriptionItems.data.isNullOrEmpty()) {
                        viewModel.updatePairs()
                        viewModel.updateNextAlarm()
                    }
                }
                is Resource.Loading -> {
                    Log.d(TAG, "Resource Loading")
                }
                else -> {
                    Log.d(TAG, "Resource Error")
                }
            }
        }

        viewModel.apply {
            drugs.observe(viewLifecycleOwner) { drugs ->
                if (!drugs.data.isNullOrEmpty()) {
                    updatePairs()
                }
            }

            pairs.observe(viewLifecycleOwner) { pairs ->
                binding.apply {
                    if (!pairs.isNullOrEmpty()) {
                        switchLayout.visibility = View.VISIBLE
                        activePrescriptionItemsLayoutFlipper.visibility = View.VISIBLE
                        textViewNoMedicine.visibility = View.GONE

                        Log.i(TAG, "Pairs Count - ${pairs.size}")
                        Log.i(TAG, "Pairs - $pairs")
                        createBadge(pairs.count { it.first.isOverdue })

                        adapter =
                            ActivePrescriptionAdapter(pairs, this@ActiveDrugListFragment, true)
                        setupViewPager()
                        gridview.adapter =
                            ActivePrescriptionAdapter(pairs, this@ActiveDrugListFragment, false)
                        gridview.layoutManager =
                            LinearLayoutManager(requireContext())
                    } else {
                        switchLayout.visibility = View.GONE
                        activePrescriptionItemsLayoutFlipper.visibility = View.GONE
                        textViewNoMedicine.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupViewPager() {
        binding.fullItemLayout.viewPager.apply {
            binding.fullItemLayout.sliderDots.removeAllViews()
            this.adapter = this@ActiveDrugListFragment.adapter
            setShowSideItems(10, 40)
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            val dots = createSlideDots()
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    for (i in 0 until adapter!!.itemCount) {
                        dots[i]?.setImageResource(R.drawable.inactive_dot)
                    }
                    dots[position]?.setImageResource(R.drawable.active_dot)
                }
            })
        }
    }

    private fun createSlideDots(): Array<ImageView?> {
        val dots = arrayOfNulls<ImageView>(adapter.itemCount)
        for (i in 0 until adapter.itemCount) {
            dots[i] = ImageView(requireContext())
            dots[i]?.setImageResource(R.drawable.inactive_dot)
            val params = LinearLayout.LayoutParams(25, 25)
            params.setMargins(12, 0, 12, 0)
            binding.fullItemLayout.sliderDots.addView(dots[i], params)
        }
        dots[0]?.setImageResource(R.drawable.active_dot)
        return dots
    }

    override fun onSeeDetailsClick(imageview: ImageView?, pair: Pair<PrescriptionItem, Drug?>) {
        seeDetailsViewModel.setPrescriptionItemDrugPair(pair)

        if (imageview != null) {
            val args =
                bundleOf(Constants.PRESCRIPTION_ITEM_IMAGE_TRANSITION to imageview.transitionName)
            val extras = FragmentNavigatorExtras(imageview to imageview.transitionName)
            findNavController().navigate(
                R.id.action_activeDrugListFragment_to_drugDetailsFragment,
                args,
                null,
                extras
            )
        } else {
            findNavController().navigate(
                R.id.action_activeDrugListFragment_to_drugDetailsFragment
            )
        }

    }

    override fun onConfirmDoseClick(pair: Pair<PrescriptionItem, Drug?>) {
        confirmIntakeViewModel.setPrescriptionItemDrugPair(pair)
        findNavController().navigate(R.id.action_activeDrugListFragment_to_newIntakeDetailsFragment)
    }

    override fun onAlarmClick(prescriptionItem: PrescriptionItem) {
        viewModel.prescriptionItems.value?.data?.find { prescriptionItem.id == it.id }?.alarm =
            prescriptionItem.alarm
        val alarmState = prescriptionItem.alarm
        viewModel.setAlarm(alarmState, prescriptionItem.id)
        val restId = when (alarmState) {
            true -> R.string.alarm_on
            else -> R.string.alarm_off
        }
        Snackbar.make(binding.root, restId, Snackbar.LENGTH_SHORT)
            .setAction(getString(R.string.OK)) {}.show()
    }

    override fun onConfirmAcquisitionClick(pair: Pair<PrescriptionItem, Drug?>) {
        confirmViewModel.setPrescriptionItemDrugPair(pair)
        findNavController().navigate(R.id.action_activeDrugListFragment_to_confirmAcquisitionFragment)
        Log.d(TAG, "NAVIGATING TO --- ConfirmAcquisition")
    }

    private fun createBadge(total: Int) {
        val bottomNavigation =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val badge = bottomNavigation.getOrCreateBadge(R.id.activeDrugListFragment)
        badge.apply {
            if (total == 0) {
                isVisible = false
                clearNumber()
            } else {
                isVisible = true
                number = total
            }
        }
    }


    // Código baseado no código de Albert Vila Calvo, retirado do website StackOverflow
    // URL - https://stackoverflow.com/questions/10098040/android-viewpager-show-preview-of-page-on-left-and-right
    private fun ViewPager2.setShowSideItems(pageMarginPx: Int, offsetPx: Int) {
        clipToPadding = false
        clipChildren = false
        offscreenPageLimit = 3

        setPageTransformer { page, position ->
            val offset = position * -(2 * offsetPx + pageMarginPx)
            if (this.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                    page.translationX = -offset
                } else {
                    page.translationX = offset
                }
            } else {
                page.translationY = offset
            }
        }
    }
}
