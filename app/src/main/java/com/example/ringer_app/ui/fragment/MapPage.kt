package com.example.ringer_app.ui.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.Navigation
import com.example.ringer_app.R
import com.example.ringer_app.databinding.FragmentMapPageBinding
import com.example.ringer_app.repository.model.Address
import com.example.ringer_app.utils.Utils
import com.example.ringer_app.repository.viewmodel.AddressViewModel
import com.example.ringer_app.repository.viewmodel.LocationViewModel
import com.example.ringer_app.utils.ConnectionLiveData
import com.example.ringer_app.utils.ConnectivityObserver
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.CameraAnimatorOptions.Companion.cameraAnimatorOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener

class MapPage : Fragment(R.layout.fragment_map_page) {

    private lateinit var binding: FragmentMapPageBinding
    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private val mLocationViewModel: LocationViewModel by activityViewModels()
    private val mAddressViewModel: AddressViewModel by activityViewModels()
    lateinit var mStatus: LiveData<ConnectivityObserver.Status>
    private var long: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_map_page, container, false)

        binding = FragmentMapPageBinding.bind(view)
        mStatus = ConnectionLiveData(requireContext()).observe().asLiveData()

        mapView = binding.mapView
        map = mapView.getMapboxMap()

        val userLocation = mLocationViewModel.address.value
        val destinationLocation = mAddressViewModel.address.value

        map.apply {
            val userPoint = userLocation?.let { it ->
                Point.fromLngLat(it.longitude, it.latitude)
            }
            val destPoint = destinationLocation?.let {
                Point.fromLngLat(it.longitude, it.latitude)
            }

            setCamera(
                CameraOptions.Builder()
                    .center(userPoint)
                    .build()
            )
            loadStyleUri(Style.MAPBOX_STREETS)

            if (userPoint != null) {
                cameraPointer(requireContext(), userPoint, R.drawable.ic_user_location)
            }

            if (destPoint != null) {
                cameraPointer(requireContext(), destPoint, R.drawable.ic_destination_location)
            }
        }

        mLocationViewModel.printLog()

        map.addOnMapClickListener { point ->

//            mStatus.observe(viewLifecycleOwner) {
//                var address: Address = Address(
//                    point.latitude(),
//                    point.longitude(),
//                    "",
//                    "",
//                    ""
//                )
//                if (it == ConnectivityObserver.Status.Available) {
            val address = Utils.getAddress(
                requireContext(),
                point.latitude(),
                point.longitude()
            )
//                }
            mAddressViewModel.setAddress(
                address
            )
//            }

            cameraPointer(requireContext(), point, R.drawable.ic_destination_location)

            Navigation.findNavController(view)
                .navigate(R.id.navigate_from_map_page_to_profile_page)
            true
        }

        animateCameraDelayed()

        return view
    }

    // Animate the camera to the user location
    private fun animateCameraDelayed() {
        mapView.camera.apply {
            val bearing = createBearingAnimator(cameraAnimatorOptions(-45.0)) {
                duration = 4000
                interpolator = AccelerateDecelerateInterpolator()
            }
            val zoom = createZoomAnimator(
                cameraAnimatorOptions(17.0) {
                    startValue(2.0)
                }
            ) {
                duration = 4000
                interpolator = AccelerateDecelerateInterpolator()
            }
            val pitch = createPitchAnimator(
                cameraAnimatorOptions(55.0) {
                    startValue(4.0)
                }
            ) {
                duration = 4000
                interpolator = AccelerateDecelerateInterpolator()
            }

            playAnimatorsSequentially(zoom, pitch, bearing)
        }
    }

    private fun cameraPointer(context: Context, point: Point, @DrawableRes resourceId: Int) {
        bitmapFromDrawableRes(
            context,
            resourceId
        )?.let {
            val annotationApi = mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager()

            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(it)
            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    companion object {
        val TAG = MapPage::class.java.toString()
    }
}