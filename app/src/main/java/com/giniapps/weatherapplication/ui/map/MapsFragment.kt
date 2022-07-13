package com.giniapps.weatherapplication.ui.map

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.giniapps.weatherapplication.R
import com.giniapps.weatherapplication.databinding.FragmentMapsBinding
import com.giniapps.weatherapplication.model.Weather
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt


@AndroidEntryPoint
class MapsFragment : Fragment() {
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private val mapViewModel: MapsViewModel by activityViewModels()

    private val gpsReceiver = object : BroadcastReceiver() {
        private var isGpsEnabled: Boolean = false
        private var isNetworkEnabled: Boolean = false

        override fun onReceive(context: Context, intent: Intent) {
            intent.action?.let { act ->
                if (act.matches("android.location.PROVIDERS_CHANGED".toRegex())) {
                    val locationManager =
                        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    isNetworkEnabled =
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                    if (!(isGpsEnabled || isNetworkEnabled)) {
                        binding.addressTv.text =
                            getString(R.string.turnGps)
                    }
                }
            }
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->

        googleMap.setOnMapClickListener { location ->
            // Setting the position for the marker
            setupMap(location, googleMap)
            mapViewModel.getCurrentWeatherByLocation(location.latitude, location.longitude)
            lifecycleScope.launchWhenStarted {
                mapViewModel.weatherUIState.collect {
                    when (it) {
                        is WeatherMapsUiState.Success -> {
                            binding.card.isVisible = true
                            updateUi(it.currentWeather)
                            binding.progressBar.isVisible = false
                        }
                        is WeatherMapsUiState.Error -> {
                            binding.card.isVisible = false
                            showDialog(it.message)
                            showSnackbar(it.message)
                            binding.progressBar.isVisible = false

                        }
                        is WeatherMapsUiState.Loading -> {
                            binding.card.isVisible = false
                            binding.progressBar.isVisible = true
                        }

                        else -> {}
                    }
                }
            }

        }
    }

    private fun showSnackbar(message:String) {
        Snackbar.make(binding.maps, message, Snackbar.LENGTH_LONG)
            .show()
    }

    private fun showDialog(message:String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            builder.setTitle(R.string.dialogTitle)
            builder.setMessage(message)
            builder.setPositiveButton("OK") { _, _ ->
            }
        }.run { show() }
    }

    private fun setupMap(
        location: LatLng,
        googleMap: GoogleMap
    ) {
        val markerOptions = MarkerOptions()
        markerOptions.position(location)
        googleMap.apply {
            clear()
            animateCamera(CameraUpdateFactory.newLatLng(location))
            addMarker(markerOptions)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)


    }

    private fun updateUi(weather: Weather) {
        binding.apply {
            degreesTv.text =
                getString(R.string.degrees, weather.temperature.roundToInt())
            Glide.with(image)
                .load(Uri.parse(weather.getImageUrl()))
                .fitCenter()
                .into(image)

            descriptionTv.text = weather.summary
            addressTv.text = weather.address
        }
    }


    override fun onStop() {
        requireContext().unregisterReceiver(gpsReceiver)
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        requireContext().registerReceiver(gpsReceiver, filter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}