package pt.ipleiria.estg.dei.pi.mymultiprev.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.seeDetails.IntakeHistoryFragment
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.seeDetails.MoreInfoFragment
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.seeDetails.PrescriptionItemDetailsFragment

class PrescriptionItemDetailsAdapter(
    private val context: Fragment
) : FragmentStateAdapter(context) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PrescriptionItemDetailsFragment()
            1 -> IntakeHistoryFragment()
            else -> MoreInfoFragment()
        }
    }
}