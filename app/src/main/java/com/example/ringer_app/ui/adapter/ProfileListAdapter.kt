package com.example.ringer_app.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ringer_app.databinding.ProfileCardBinding
import com.example.ringer_app.repository.model.Profile

class ProfileListAdapter(
    private val profileList: List<Profile>,
    private val updateActivation: (profile: Profile) -> Unit
) :
    RecyclerView.Adapter<ProfileListAdapter.ProfileListViewHolder>() {

    inner class ProfileListViewHolder(private val cardBinding: ProfileCardBinding) :
        RecyclerView.ViewHolder(cardBinding.root) {
        fun bindItem(profile: Profile, updateActivation: (profile: Profile) -> Unit) {
            cardBinding.tvProfileTitle.text = profile.name
            cardBinding.tvRingerMode.text = profile.ringerMode
            cardBinding.tvLocation.text = profile.location.address
            cardBinding.btnProfileToggle.isChecked = profile.isActive

            cardBinding.btnProfileToggle.setOnCheckedChangeListener { _, isChecked ->
                val message = if (isChecked) "Switch1:ON" else "Switch1:OFF"

                Log.d("Adapter", "bindItem: $message")
                updateActivation(
                    Profile(
                        profile.id,
                        profile.name,
                        profile.ringerMode,
                        profile.startTime,
                        profile.stopTime,
                        profile.location,
                        profile.radius,
                        isChecked
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileListViewHolder {
        return ProfileListViewHolder(
            ProfileCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProfileListViewHolder, position: Int) {
        val profile = profileList[position]
        holder.bindItem(profile, updateActivation)
    }

    override fun getItemCount(): Int {
        return profileList.count()
    }
}