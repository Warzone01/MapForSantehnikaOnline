package com.example.mapforsantehnikaonline.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.mapforsantehnikaonline.R
import com.example.mapforsantehnikaonline.databinding.FragmentMapPointInfoBinding
import com.example.mapforsantehnikaonline.models.MainViewModel
import com.example.mapforsantehnikaonline.utils.ClickListener
import com.example.mapforsantehnikaonline.utils.Constants
import com.example.mapforsantehnikaonline.utils.StringBuilder
import java.lang.ClassCastException

class MapPointInfoFragment : Fragment() {

    private lateinit var binding: FragmentMapPointInfoBinding
    private lateinit var model: MainViewModel
    private lateinit var clickListener: ClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ClickListener) {
            clickListener = context
        } else {
            throw ClassCastException()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let {
            model = ViewModelProvider(it, defaultViewModelProviderFactory)
                .get(MainViewModel::class.java)
        }
        binding = FragmentMapPointInfoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setClicks()

        // следит за изменением в liveData
        model.address.observe(this, {
            setCoordinatesText(StringBuilder.getFullAddress(model.address.value))
        })
    }

    // устанавливает координаты в textView
    private fun setCoordinatesText(coordinates: String) {
        binding.tvSelectedCoordinates.apply {
            text = if (coordinates.isNotBlank()) {
                getString(R.string.selected_coordinates, coordinates)
            } else {
                getString(R.string.point_not_set)
            }
        }
    }

    // устанавливает клики
    private fun setClicks() {
        binding.btnShowOnMap.setOnClickListener {
            clickListener.onNavigationButtonClicked(
                fragment = MapFragment.newInstance(Bundle()),
                fragmentName = Constants.MAP_FRAGMENT
            )
        }
    }

    companion object {
        fun newInstance(bundle: Bundle): MapPointInfoFragment {
            val fragment = MapPointInfoFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}