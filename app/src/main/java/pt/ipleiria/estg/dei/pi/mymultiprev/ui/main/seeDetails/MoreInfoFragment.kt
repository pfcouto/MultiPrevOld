package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.seeDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.databinding.FragmentMoreInfoBinding


class MoreInfoFragment : Fragment() {
    private lateinit var binding: FragmentMoreInfoBinding
    private val viewModel: SeeDetailsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMoreInfoBinding.bind(view)
        binding.drug = viewModel.drug.value
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_more_info, container, false)
    }
}