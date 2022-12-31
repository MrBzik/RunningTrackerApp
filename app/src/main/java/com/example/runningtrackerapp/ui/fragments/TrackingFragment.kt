package com.example.runningtrackerapp.ui.fragments


import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.databinding.FragmentTrackingBinding
import com.example.runningtrackerapp.db.entities.Run
import com.example.runningtrackerapp.services.Polyline
import com.example.runningtrackerapp.services.TrackingService
import com.example.runningtrackerapp.ui.MainActivity
import com.example.runningtrackerapp.ui.MainViewModel
import com.example.runningtrackerapp.utils.Constants.COMMAND_PAUSE_SERVICE
import com.example.runningtrackerapp.utils.Constants.COMMAND_START_SERVICE
import com.example.runningtrackerapp.utils.Constants.COMMAND_STOP_SERVICE
import com.example.runningtrackerapp.utils.Constants.MAP_ZOOM
import com.example.runningtrackerapp.utils.Constants.POLYLINE_COLOR
import com.example.runningtrackerapp.utils.Constants.POLYLINE_WIDTH
import com.example.runningtrackerapp.utils.Utils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.round

const val CANCEL_DIALOG_TAG = "SomeDIalogTag"

@AndroidEntryPoint
class TrackingFragment : BaseFragment<FragmentTrackingBinding> (
    FragmentTrackingBinding::inflate
        ), MenuProvider {


    @Inject
    lateinit var userName : String

    private val viewModel : MainViewModel by viewModels()

    private var map: GoogleMap? = null

    private var isTracking = false

    private var pathPoints = mutableListOf<Polyline>()

    private var currentTimeMills = 0L

    private var menu : Menu? = null

    @set:Inject
    var weight = 90.0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val menuHost : MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return super.onCreateView(inflater, container, savedInstanceState)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind.mapView.onCreate(savedInstanceState)

        bind.mapView.getMapAsync{

            map = it

            drawAllPolylines()
        }

        bind.btnToggleRun.setOnClickListener {

            toggleRun()

        }

        subscribeToObservers()


        bind.btnFinishRun.setOnClickListener {

            prepareCamForFinalPicture()
            endRunAndSaveToDB()

        }


    }

    private fun subscribeToObservers(){

        TrackingService.isTracking.observe(viewLifecycleOwner){

            updateTracking(it)

        }

        TrackingService.pathPoints.observe(viewLifecycleOwner){

            pathPoints = it

            moveCameraToUser()
            drawPolylineLast()

        }

        TrackingService.runTimeInMillis.observe(viewLifecycleOwner){

            currentTimeMills = it

            bind.tvTimer.text = Utils.timerFormat(it, true)
        }

        TrackingService.currentSpeed.observe(viewLifecycleOwner){

            (activity as MainActivity).toolbarText.text = "Speed : $it km/h"


        }

        TrackingService.distanceSoFar.observe(viewLifecycleOwner){

            var distance = round(it).toInt()
            var displayMode = " m"

            if(it<1000){

                bind.tvDistanceSoFar?.text = "$distance$displayMode"

            }   else {

                val distanceInKm = distance / 1000f
                displayMode = " km"
                bind.tvDistanceSoFar?.text = "$distanceInKm$displayMode"
            }
        }
    }

    private fun toggleRun() {

        if(isTracking) {

            sendCommandToService(COMMAND_PAUSE_SERVICE)
        }

        else {
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(COMMAND_START_SERVICE)
        }


    }

    private fun moveCameraToUser(){

        if(pathPoints.isNotEmpty()){
            if(pathPoints.last().isNotEmpty())

                map?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        pathPoints.last().last(), MAP_ZOOM)
                )
        }
    }


    private fun updateTracking(isTracking : Boolean){
        this.isTracking = isTracking

        if(!isTracking && currentTimeMills > 0) {
            bind.btnToggleRun.text = "Start"
            bind.btnFinishRun.visibility = View.VISIBLE

        } else if(isTracking) {
            bind.btnToggleRun.text = "STOP"
            bind.btnFinishRun.visibility = View.GONE
            menu?.getItem(0)?.isVisible = true
        }


    }


 private fun drawAllPolylines(){

     for (polyline in pathPoints) {

        val polylineOptions = PolylineOptions()
            .color(POLYLINE_COLOR)
            .width(POLYLINE_WIDTH)
            .addAll(polyline)

           map?.addPolyline(polylineOptions)

     }
 }

    private fun drawPolylineLast(){

        if(pathPoints.isNotEmpty()){
            if(pathPoints.last().size > 1) {

                val preLastPoint = pathPoints.last()[pathPoints.last().size - 2]
                val lastPoint = pathPoints.last().last()

                val polylineOptions = PolylineOptions()
                    .color(POLYLINE_COLOR)
                    .width(POLYLINE_WIDTH)
                    .add(preLastPoint)
                    .add(lastPoint)

                    map?.addPolyline(polylineOptions)
            }
        }
    }


    private fun prepareCamForFinalPicture(){

        val bound = LatLngBounds.builder()

        for(polyline in pathPoints){
            for(coordinates in polyline){
                bound.include(coordinates)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bound.build(),
                bind.mapView.width,
                bind.mapView.height,
                (bind.mapView.width*0.08f).toInt()
            )
        )
    }

    private fun endRunAndSaveToDB(){

        var bitmap : Bitmap

        map?.snapshot{ bmp ->

            var distanceInMeters = 0

            TrackingService.distanceSoFar.value?.let {

                distanceInMeters = it.toInt()
            }

            val averageSpeed = round((distanceInMeters/1000f) / (currentTimeMills/ 1000f / 60 / 60 ) *10 ) /10f


            val dateTimeStamp = Calendar.getInstance().timeInMillis

            val caloriesBurned = ((currentTimeMills / 1000f / 60) * 8).toInt()

            val run = Run(bmp, dateTimeStamp, currentTimeMills, distanceInMeters, averageSpeed, caloriesBurned)

            viewModel.insertRun(run)

            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Run was saved", Snackbar.LENGTH_LONG).show()

            cancelRun()

           }
        }


    override fun onResume() {
        super.onResume()
        bind.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        bind.mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        bind.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        bind.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        bind.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        bind.mapView.onSaveInstanceState(outState)



    }

    private fun sendCommandToService (command : String) {

        Intent(requireContext(), TrackingService::class.java).also {
            it.action = command
            requireContext().startService(it)
        }

    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

        menuInflater.inflate(R.menu.tracking_cancel_menu, menu)

            this.menu = menu
    }

    override fun onPrepareMenu(menu: Menu) {
        super.onPrepareMenu(menu)
        if(currentTimeMills>0L) {

            this.menu?.getItem(0)?.isVisible=true
        }
    }

    private fun showCancelRunDialog(){

        CancelDialogFragment(){
           cancelRun()

        }.show(parentFragmentManager, CANCEL_DIALOG_TAG)

    }

    private fun cancelRun(){

        (activity as MainActivity).toolbarText.text = "Let's go, $userName!"
        sendCommandToService(COMMAND_STOP_SERVICE)
        bind.tvTimer.text = "00:00:00:00"
        bind.tvDistanceSoFar?.text = "0 m"
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

       when(menuItem.itemId){
           R.id.miCancelRun -> showCancelRunDialog()
       }

        return false
    }

}