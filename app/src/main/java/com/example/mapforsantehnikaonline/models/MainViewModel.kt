package com.example.mapforsantehnikaonline.models

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapforsantehnikaonline.utils.StringBuilder
import java.io.IOException
import java.util.*

class MainViewModel : ViewModel() {
    val address: MutableLiveData<Address> = MutableLiveData()

    // получить адрес
    fun getAddressName(latitude: Double, longitude: Double, context: Context?): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses: MutableList<android.location.Address>? =
                geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val returnedAddress = addresses[0]

                // строит основную строку
                StringBuilder.addressNameBuilder(
                    countryName = returnedAddress.countryName,
                    adminArea = returnedAddress.adminArea,
                    locality = returnedAddress.locality,
                    thoroughfare = returnedAddress.thoroughfare,
                    subThoroughfare = returnedAddress.subThoroughfare
                )
            } else {
                StringBuilder.EMPTY_STRING
            }
        } catch (e: IOException) {
            e.printStackTrace()
            StringBuilder.EMPTY_STRING
        }
    }
}