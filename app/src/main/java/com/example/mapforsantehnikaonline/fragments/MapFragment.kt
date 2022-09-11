package com.example.mapforsantehnikaonline.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mapforsantehnikaonline.databinding.FragmentMapBinding
import com.example.mapforsantehnikaonline.models.Address
import com.example.mapforsantehnikaonline.models.MainViewModel
import com.example.mapforsantehnikaonline.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView

class MapFragment : Fragment(), InputListener {

    private lateinit var binding: FragmentMapBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapView: MapView
    private lateinit var model: MainViewModel

    private var placemark: PlacemarkMapObject? = null
    private var longitude: Double? = null
    private var latitude: Double? = null
    private var addressName: String? = null
    private var zoom: Float = 18.0f
    private var azimuth: Float = 0.0f
    private var tilt: Float = 0.0f

    private var duration: Float = 0.5f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let {
            model = ViewModelProvider(
                it,
                defaultViewModelProviderFactory
            ).get(MainViewModel::class.java)
        }
        binding = FragmentMapBinding.inflate(layoutInflater, container, false)
        mapView = binding.mvMainMap
        savedInstanceState?.let {
            restoreState(it)
        }
        context?.let {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(it)
            if (longitude != null && latitude != null) {
                changeMapLocation(
                    longitude = longitude ?: Constants.DEFAULT_COORDINATES,
                    latitude = latitude ?: Constants.DEFAULT_COORDINATES
                )
            } else {
                getLastKnownLocation(it)
            }

        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        mapView.map.addInputListener(this)
        setClickListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.apply {
            latitude?.let {
                putString(Constants.LATITUDE, it.toString())
            }
            longitude?.let {
                putString(Constants.LONGITUDE, it.toString())
            }
            addressName?.let {
                putString(Constants.ADDRESS_NAME, addressName)
            }
            putFloat(Constants.ZOOM, mapView.map.cameraPosition.zoom)
            putFloat(Constants.AZIMUTH, mapView.map.cameraPosition.azimuth)
            putFloat(Constants.TILT, mapView.map.cameraPosition.tilt)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onMapTap(p0: Map, p1: Point) {
        placemark?.let { p0.mapObjects.remove(it) }
        placemark = p0.mapObjects.addPlacemark(p1)
        longitude = p1.longitude
        latitude = p1.latitude
        addressName = model.getAddressName(
            longitude = p1.longitude,
            latitude = p1.latitude,
            context = activity?.baseContext
        )
        changeButtonVisibility()
    }

    override fun onMapLongTap(p0: Map, p1: Point) {
        onMapTap(p0, p1)
    }

    // получить текущую локацию и показать её
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(context: Context) {
        if (isHasNoPermission(context)) {
            return
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        duration = 0.5f
                        changeMapLocation(
                            longitude = it.longitude,
                            latitude = it.latitude
                        )
                    }
                }
        }
    }

    // передвинуть камеру на необходимые координаты
    private fun changeMapLocation(longitude: Double, latitude: Double) {
        mapView.map.move(
            CameraPosition(
                Point(latitude, longitude),
                zoom,
                azimuth,
                tilt
            ),
            Animation(Animation.Type.SMOOTH, duration),
            null
        )
    }

    // устанавливает клики
    private fun setClickListeners() {
        binding.btnApplyMapPoint.setOnClickListener {
            model.address.value = Address(
                latitude = latitude,
                longitude = longitude,
                addressName = addressName ?: ""
            )
            activity?.onBackPressed()
        }
    }

    // устанавливает видимость кнопки
    private fun changeButtonVisibility() = if (latitude == null || longitude == null) {
        binding.btnApplyMapPoint.visibility = View.GONE
    } else {
        binding.btnApplyMapPoint.visibility = View.VISIBLE
    }

    // проверка разрешений
    private fun isHasNoPermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    }

    // восстанавливает приложение после смены состояния
    private fun restoreState(bundle: Bundle) {
        duration = 0f
        longitude = bundle.getString(Constants.LONGITUDE)?.toDouble()
        latitude = bundle.getString(Constants.LATITUDE)?.toDouble()
        zoom = bundle.getFloat(Constants.ZOOM, zoom)
        azimuth = bundle.getFloat(Constants.AZIMUTH, azimuth)
        tilt = bundle.getFloat(Constants.TILT, tilt)
        addressName = bundle.getString(Constants.ADDRESS_NAME, "")
        if (longitude != null && latitude != null) {
            placemark = mapView.map.mapObjects.addPlacemark(
                Point(
                    latitude ?: Constants.DEFAULT_COORDINATES,
                    longitude ?: Constants.DEFAULT_COORDINATES
                )
            )
        }
        changeButtonVisibility()
    }

    companion object {
        fun newInstance(bundle: Bundle): MapFragment {
            val fragment = MapFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}