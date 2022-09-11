package com.example.mapforsantehnikaonline.utils

import androidx.fragment.app.Fragment

interface ClickListener {
    fun onNavigationButtonClicked(fragment: Fragment, fragmentName: String)
}