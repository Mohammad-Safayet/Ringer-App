package com.example.ringer_app.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.ringer_app.DummyData
import com.example.ringer_app.R
import com.example.ringer_app.ui.adapter.ProfileListAdapter
import com.example.ringer_app.databinding.FragmentLandingPageBinding
import com.example.ringer_app.repository.viewmodel.ProfileViewModel
import com.example.ringer_app.repository.model.Profile
import com.example.ringer_app.repository.viewmodel.LocationViewModel
import com.example.ringer_app.service.RingerAppService.Companion.address

class LandingPage : Fragment(R.layout.fragment_landing_page) {

    private lateinit var mBinding: FragmentLandingPageBinding
    private val mProfileViewModel: ProfileViewModel by activityViewModels()
    private val mLocationViewModel: LocationViewModel by activityViewModels()

    private val swipeToDelete = object :
        ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            val position = viewHolder.adapterPosition;

            when (direction) {
                ItemTouchHelper.LEFT -> {
                    val profile = mProfileViewModel.profiles.value?.get(position)

                    if (profile != null) {
                        mProfileViewModel.deleteProfile(profile)
                    }

                    Log.d(TAG, "onSwiped: ${mProfileViewModel.profiles.value}")
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_landing_page, container, false)

        mBinding = FragmentLandingPageBinding.bind(view)

        mLocationViewModel.address.observe(viewLifecycleOwner) {
            Log.d(TAG, "onCreateView: $it")
            if (it == null) {
                mBinding.progressBar.isVisible = true
                mBinding.viewProfileList.isVisible = false
                mBinding.tvDefaultText.isVisible = false
                mBinding.btnAddProfile.isClickable = false
            } else {
                mBinding.progressBar.isVisible = false
                mBinding.viewProfileList.isVisible = true
                mBinding.btnAddProfile.isClickable = true

                mProfileViewModel.profiles.observe(viewLifecycleOwner) { profiles: List<Profile> ->
                    if (profiles.isEmpty()) {
                        mBinding.tvDefaultText.isVisible = true
                        return@observe
                    }

                    mBinding.tvDefaultText.isVisible = false
                    mBinding.viewProfileList.adapter =
                        ProfileListAdapter(profiles, this::toggleProfile)
                }

                val itemTouchHelper = ItemTouchHelper(swipeToDelete)
                itemTouchHelper.attachToRecyclerView(mBinding.viewProfileList)

                mBinding.btnAddProfile.setOnClickListener {
                    Navigation.findNavController(view)
                        .navigate(R.id.navigate_from_landing_page_to_profile_page)
                }
            }
        }

        return view
    }

    private fun toggleProfile(profile: Profile) {
        mProfileViewModel.updateProfile(profile)
    }

    companion object {
        val TAG = LandingPage::class.java.toString()
    }
}