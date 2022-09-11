package com.example.mapforsantehnikaonline.utils

import com.example.mapforsantehnikaonline.models.Address

object StringBuilder {
    const val EMPTY_STRING = ""

    // построить строковый вариант адреса
    fun addressNameBuilder(
        countryName: String?,
        adminArea: String?,
        locality: String?,
        thoroughfare: String?,
        subThoroughfare: String?
    ): String {
        val result = buildString {
            countryName?.let {
                this.append(it)
            }

            adminArea?.let {
                this.append(", $it")
            }

            locality?.let {
                this.append(", $it")
            }

            thoroughfare?.let {
                this.append(", $it")
            }

            subThoroughfare?.let {
                this.append(", $it")
            }
        }
        return result
    }

    // возвращает полный адрес строкой
    fun getFullAddress(address: Address?): String {
        address?.let {
            return "${it.addressName} (${it.latitude}, ${it.longitude})"
        }
        return EMPTY_STRING
    }
}