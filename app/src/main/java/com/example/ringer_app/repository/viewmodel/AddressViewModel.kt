package com.example.ringer_app.repository.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ringer_app.repository.model.Address

class AddressViewModel(application: Application): AndroidViewModel(application) {
    private val TAG = AddressViewModel::class.java.toString()

    private val _address: MutableLiveData<Address> = MutableLiveData<Address>()
    private val _name: MutableLiveData<String> = MutableLiveData<String>()
    private val _ringerMode: MutableLiveData<String> = MutableLiveData<String>()
    private val _startTime: MutableLiveData<String> = MutableLiveData<String>()
    private val _stopTime: MutableLiveData<String> = MutableLiveData<String>()

    val address: LiveData<Address> = _address
    val name: LiveData<String> = _name
    val ringerMode: LiveData<String> = _ringerMode
    val startTime: LiveData<String> = _startTime
    val stopTime: LiveData<String> = _stopTime

    fun setAddress(address: Address) {
        _address.value = address
        Log.d(TAG, " onPrintLog : ${_address.value}")
    }

    fun setTime(name: String, ringerMode: String, startTime: String, stopTime: String) {
        _name.value = name
        _ringerMode.value = ringerMode
        _startTime.value = startTime
        _stopTime.value = stopTime
    }

    fun reset() {
        _name.value = ""
        _ringerMode.value = ""
        _startTime.value = ""
        _stopTime.value = ""
        _address.value = Address(0.0, 0.0, "", "", "")
    }
}