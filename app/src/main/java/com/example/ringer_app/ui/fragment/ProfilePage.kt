package com.example.ringer_app.ui.fragment

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.ringer_app.R
import com.example.ringer_app.databinding.FragmentProfilePageBinding
import com.example.ringer_app.repository.model.Profile
import com.example.ringer_app.repository.viewmodel.AddressViewModel
import com.example.ringer_app.repository.viewmodel.ProfileViewModel
import com.example.ringer_app.utils.Utils
import com.example.ringer_app.utils.Utils.formatEqn1
import com.example.ringer_app.utils.Utils.formatEqn2
import com.example.ringer_app.utils.Utils.formatEqn3
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class ProfilePage : Fragment(R.layout.fragment_profile_page) {

    private lateinit var binding: FragmentProfilePageBinding
    private val mAddressViewModel: AddressViewModel by activityViewModels()
    private val mProfileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        val modes = resources.getStringArray(R.array.ringer_modes)
        binding.inputRingerMode.setAdapter(
            ArrayAdapter(requireActivity(), R.layout.ringer_mode_dropdown_item, modes)
        )
    }

    //TODO: add Logic about start time and end time being equal or negative
    private fun openTimePicker(title: String, isStartTime: Boolean) {
        val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(12)
            .setMinute(0)
            .setTitleText(title)
            .build()
        picker.show(childFragmentManager, "TAG")
        var time = ""

        picker.addOnPositiveButtonClickListener {

            time = "${picker.hour}:${picker.minute}"

            val date = formatEqn1.parse(time)
            time = formatEqn2.format(date!!)

            if (isStartTime) {
                binding.inputStartTime.setText(time)
            } else {
                val startTime = binding.inputStartTime.text.toString()
                Log.d(TAG, "openTimePicker: $startTime $time")
                if (startTime.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please select start time first.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
//                    Utils.getTimeDifference(startTime, time)
                    binding.inputEndTime.setText(time)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile_page, container, false)

        binding = FragmentProfilePageBinding.bind(view);


        binding.inputProfileName.setText(mAddressViewModel.name.value)
        binding.inputRingerMode.setText(mAddressViewModel.ringerMode.value)
        binding.inputStartTime.setText(mAddressViewModel.startTime.value)
        binding.inputEndTime.setText(mAddressViewModel.stopTime.value)

        // Material Time Picker
        binding.inputStartTime.setOnClickListener {
            openTimePicker("Start Time", true)
        }
        binding.inputEndTime.setOnClickListener {
            openTimePicker("End Time", false)
        }

        binding.inputLocation.setOnClickListener {
            mAddressViewModel.setTime(
                binding.inputProfileName.text.toString(),
                binding.inputRingerMode.text.toString(),
                binding.inputStartTime.text.toString(),
                binding.inputEndTime.text.toString()
            )

            Navigation.findNavController(view).navigate(R.id.navigate_from_profile_page_to_map_page)
        }

        mAddressViewModel.address.observe(requireActivity()) {
            Log.d(TAG, "onCreateView: ${it}")
            binding.inputLocation.setText(it!!.address)
        }

        binding.btnConfirmProfile.setOnClickListener {
            addProfile()
            mAddressViewModel.reset()

            Navigation.findNavController(view)
                .navigate(R.id.navigate_from_profile_page_to_landing_page)
        }

        return view;
    }

    private fun addProfile() {

        val name = binding.inputProfileName.text.toString()
        val ringerMode = binding.inputRingerMode.text.toString()
        val startTime = formatEqn2.parse(binding.inputStartTime.text.toString())
            ?.let { formatEqn3.format(it) }
        val stopTime = formatEqn2.parse(binding.inputEndTime.text.toString())
            ?.let { formatEqn3.format(it) }
        val location = mAddressViewModel.address.value
        val radius = binding.inputRadius.text.toString().toDouble()

        Log.d(TAG, "onCreateView: $name, $ringerMode, $startTime, $stopTime, $location")

        location?.let { address ->
            Profile(
                0,
                name,
                ringerMode,
                startTime!!,
                stopTime!!,
                address,
                radius,
                true
            )
        }?.let { profile -> mProfileViewModel.addProfile(profile) }
    }

    companion object {
        val TAG = ProfilePage::class.java.toString()
    }
}