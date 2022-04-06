package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.seeDetails

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.adapters.PrescriptionItemDetailsAdapter
import pt.ipleiria.estg.dei.pi.mymultiprev.databinding.ChangeAliasDialogBinding
import pt.ipleiria.estg.dei.pi.mymultiprev.databinding.SeeDetailsFragmentBinding
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.camera.CameraActivity
import pt.ipleiria.estg.dei.pi.mymultiprev.util.CameraResult
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants.REQUEST_CODE_PERMISSIONS
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants.REQUIRED_PERMISSIONS


@AndroidEntryPoint
class SeeDetailsFragment() : Fragment() {

    private val TAG = "SeeDetailsFragment"

    private lateinit var binding: SeeDetailsFragmentBinding
    private val viewModel: SeeDetailsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.see_details_fragment, container, false)

        setupTransitionAnimation(layout)

        return layout
    }

    private fun setupTransitionAnimation(layout: View) {
        layout.findViewById<FrameLayout>(R.id.prescription_item_image_layout).transitionName =
            arguments?.getString(Constants.PRESCRIPTION_ITEM_IMAGE_TRANSITION)

        val animation =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "### Register Symptoms Fragment Created ###")

        activity?.findViewById<TextView>(R.id.textViewFragmentTitle)?.visibility = View.GONE
        binding = SeeDetailsFragmentBinding.bind(view)

        viewModel.getPrescriptionItemPhoto(viewModel.prescriptionItem.value!!.id)

        binding.prescriptionItem = viewModel.prescriptionItem.value
        binding.drug = viewModel.drug.value

        viewModel.photoUri.observe(viewLifecycleOwner) {
            if (it == null)
                Glide.with(this).load(R.drawable.default_img).into(binding.expandedImage)
            else
                Glide.with(this).load(it).into(binding.expandedImage)
        }

        binding.toolbarLayout.setOnClickListener {
            val alert = createChangeAliasDialog()
            alert.show()
        }


        binding.detailsPager.adapter = PrescriptionItemDetailsAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.detailsPager) { tab, position ->
            Log.i(TAG, "Tab position $position")
            tab.text = when (position) {
                0 -> getString(R.string.see_details_tab_details)
                1 -> getString(R.string.see_details_tab_intake_history)
                else -> getString(R.string.more_info)
            }
            binding.detailsPager.currentItem = 0
        }.attach()
    }

    private fun createChangeAliasDialog(): AlertDialog.Builder {
        val alert = AlertDialog.Builder(requireContext())
        val dialogViewBinding = ChangeAliasDialogBinding.inflate(layoutInflater)

        dialogViewBinding.drug = viewModel.drug.value

        alert.setTitle(getString(R.string.change_alias_dialog_title))
        alert.setMessage(getString(R.string.change_alias_dialog_desc))

        alert.setPositiveButton(
            getString(R.string.confirm)
        ) { _, _ ->
            val alias = dialogViewBinding.editTextUsername.text.toString()
            binding.toolbarLayout.title = alias

            viewModel.setPrescriptionItemAlias(viewModel.drug.value!!.id, alias)
        }

        alert.setNegativeButton(
            getString(R.string.cancel)
        ) { _, _ ->

        }
        alert.setView(dialogViewBinding.root)

        return alert
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.btnChangeImage.setOnClickListener {
            if (allPermissionsGranted()) {
                val intent = Intent(requireActivity(), CameraActivity::class.java)
                intent.putExtra(
                    Constants.PRESCRIPTION_ITEM_ID,
                    viewModel.prescriptionItem.value?.id
                )
                startActivityForResult(
                    intent,
                    Constants.CAMERA_ACTIVITY_RESULT
                )
            } else {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.CAMERA_ACTIVITY_RESULT) {
            when (resultCode) {
                CameraResult.RESULT_OK -> {
                    viewModel.getPrescriptionItemPhoto(data?.getStringExtra(Constants.PRESCRIPTION_ITEM_ID)!!)
                    displaySnackbar(R.string.photo_taken_success)
                }
                CameraResult.RESULT_ERROR -> {
                    displaySnackbar(R.string.photo_taken_error)
                }
                else -> {

                }
            }
        }
    }

    private fun displaySnackbar(messageResId: Int) {
        Snackbar.make(
            binding.root,
            getString(messageResId),
            Snackbar.LENGTH_SHORT
        )
            .setAction(getString(R.string.OK)) {}
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                val intent = Intent(requireActivity(), CameraActivity::class.java)
                intent.putExtra(
                    Constants.PRESCRIPTION_ITEM_ID,
                    viewModel.prescriptionItem.value?.id
                )
                startActivityForResult(
                    intent,
                    Constants.CAMERA_ACTIVITY_RESULT
                )
            } else {
                displaySnackbar(R.string.no_permissions_granted)
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }
}