package com.example.mapforsantehnikaonline.activities

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.mapforsantehnikaonline.R
import com.example.mapforsantehnikaonline.databinding.ActivityMainBinding
import com.example.mapforsantehnikaonline.fragments.MapPointInfoFragment
import com.example.mapforsantehnikaonline.utils.ClickListener
import com.example.mapforsantehnikaonline.utils.Constants
import com.example.mapforsantehnikaonline.utils.Constants.MAP_POINT_INFO_FRAGMENT
import com.example.mapforsantehnikaonline.utils.MapKitInitializer

class MainActivity : AppCompatActivity(), ClickListener {

    private lateinit var binding: ActivityMainBinding
    private var fragmentName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitInitializer.initialize(
            apiKey = "YOUR_KEY",
            context = this
        )
        fragmentName = savedInstanceState?.getString(Constants.FRAGMENT_NAME)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (fragmentName == null) {
            setFragment(
                fragment = MapPointInfoFragment.newInstance(Bundle()),
                isAdd = false,
                nameFragment = MAP_POINT_INFO_FRAGMENT
            )
        }
    }

    override fun onResume() {
        super.onResume()

        // получаем необходимые разрешения для получения локации
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(Constants.FRAGMENT_NAME, fragmentName)
        super.onSaveInstanceState(outState)
    }

    override fun onNavigationButtonClicked(fragment: Fragment, fragmentName: String) {
        setFragment(
            fragment = fragment,
            isAdd = true,
            nameFragment = fragmentName
        )
    }

    // установить фрагмент
    private fun setFragment(fragment: Fragment, isAdd: Boolean, nameFragment: String) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentName = nameFragment

        if (isAdd) {
            transaction
                .add(R.id.main_fragment, fragment)
                .addToBackStack(nameFragment)
                .commit()
        } else {
            transaction
                .replace(R.id.main_fragment, fragment)
                .commit()
        }
    }
}