package com.example.aroundog.fragments

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.setFragmentResult
import com.example.aroundog.R
import com.example.aroundog.SerialLatLng
import com.example.aroundog.RealtimeLocation
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.concurrent.timer


class MainFragment : Fragment(), OnMapReadyCallback{

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private var pathList:ArrayList<LatLng> = ArrayList<LatLng>()
    var serialPathList:ArrayList<SerialLatLng> = ArrayList<SerialLatLng>()
    private var pathOverlay: PathOverlay = PathOverlay()
    private var isStart:Boolean = false
    private var isFirst:Boolean = true
    lateinit var overlayImage: OverlayImage
    lateinit var compassImage: OverlayImage
    lateinit var lastLocation: Location
    var walkDistance:Double = 0.0
    val TAG = "MainFragmentTAG"

    lateinit var frame:FrameLayout
    lateinit var startWalkButton:Button
    lateinit var walkDistanceTV:TextView
    lateinit var walkTimeTV:TextView
    lateinit var pauseButton:ImageButton
    lateinit var statusLayout:LinearLayout

    lateinit var timer:Timer
    var time:Long = 0

    val realdb:RealtimeLocation = RealtimeLocation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var mapView = parentFragmentManager.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                parentFragmentManager.beginTransaction().add(R.id.map, it, "map").commit()
            }
        mapView.getMapAsync(this)

        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        locationSource.isCompassEnabled = true // ????????? ?????? ??????

        overlayImage = OverlayImage.fromAsset("logo.png")

        pathOverlaySettings()

        realdb.initializeDbRef()

    }

    override fun onCreateView(//?????????????????? ??????????????? ??????
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        if(parentFragmentManager.findFragmentByTag("map") != null)
//            parentFragmentManager.beginTransaction().show(parentFragmentManager.findFragmentByTag("map")!!).commit()

        val view:ViewGroup = inflater.inflate(R.layout.fragment_main,container,false) as ViewGroup
        startWalkButton = view.findViewById(R.id.startWalkButton)
        walkTimeTV = view.findViewById(R.id.walkTimeTV)
        walkDistanceTV = view.findViewById(R.id.walkDistanceTV)
        pauseButton = view.findViewById(R.id.pauseButton)
        statusLayout = view.findViewById(R.id.statusLayout)
        frame = view.findViewById(R.id.map)

        //???????????? ?????? ?????? ?????????
        startWalkButton.setOnClickListener {
            Log.d(TAG, "???????????? ?????? ??????")
            isStart = true
            pathList.add(LatLng(lastLocation))//???????????? ??????
            serialPathList.add(SerialLatLng(LatLng(lastLocation)))
            startWalk()

        }

        //???????????? ???????????? ?????????
        pauseButton.setOnClickListener {
            //???????????? ??????????????? ???????????????
            Toast.makeText(activity,"????????????", Toast.LENGTH_SHORT).show()
            isStart = false



            var bundle:Bundle = Bundle()
            bundle.putSerializable("arraylist", LatLngToSerial())
            //bundle.putSerializable("arraylist", serialPathList)
            setFragmentResult("walkEnd",bundle)
            parentFragmentManager.beginTransaction().add(R.id.main_container, EndWalkFragment(), "endWalk").addToBackStack(null).commit()


            endWalk()
        }

        return view
    }
    fun LatLngToSerial(): ArrayList<SerialLatLng> {
        var tempList = ArrayList<SerialLatLng>()
        var iterator = pathList.iterator()
        while(iterator.hasNext()){
            var temp = SerialLatLng(iterator.next())
            tempList.add(temp)
        }
        return tempList
    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
    fun startWalk(){
        statusLayout.visibility=View.VISIBLE
        startWalkButton.visibility=View.GONE
        frame.layoutParams.height=0
        startTimer()
    }

    fun startTimer(){
        timer = kotlin.concurrent.timer(period = 1000){
            time++
            setTimer()
        }
    }
    fun stopTimer(){
        timer.cancel()
    }
    fun resetTimer(){
        timer.cancel()
        time=0
        setTimer()
    }
    fun setTimer(){
        var hour = TimeUnit.SECONDS.toHours(time)
        var minute = TimeUnit.SECONDS.toMinutes(time) - hour*60
        var second = TimeUnit.SECONDS.toSeconds(time) - hour*3600 - minute*60
        walkTimeTV.text = String.format("%02d",hour) + " : " + String.format("%02d",minute) + " : "  + String.format("%02d",second)
    }

    fun pathOverlaySettings(){
        pathOverlay.outlineWidth=0//????????? ??????
        pathOverlay.width=20//????????? ???
        pathOverlay.passedColor = Color.RED//????????? ?????????
        pathOverlay.color= Color.GREEN//????????? ??????
    }

    fun endWalk(){
        pathList.clear()
        pathOverlay.map=null
        walkDistance = 0.0
        //???????????? time ??????
        statusLayout.visibility=View.GONE
        startWalkButton.visibility=View.VISIBLE

        val layout:ViewGroup.LayoutParams = frame.layoutParams
        layout.width=ViewGroup.LayoutParams.MATCH_PARENT
        layout.height=ViewGroup.LayoutParams.MATCH_PARENT
        frame.layoutParams = layout
        resetTimer()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // ?????? ?????????
                naverMap.locationTrackingMode = LocationTrackingMode.None
                Log.d(TAG, "????????????")

                return
            }
            else{

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun uiSettings(){
        //naverMap.uiSettings.isCompassEnabled=true
        naverMap.uiSettings.isLocationButtonEnabled=true//???????????? ?????? ??????
        naverMap.uiSettings.isZoomControlEnabled=false//??? ?????? ??????
    }

    fun setlocationOverlay(): LocationOverlay {
        var locationOverlay: LocationOverlay = naverMap.locationOverlay
        locationOverlay.icon=overlayImage
        locationOverlay.iconHeight = 100
        locationOverlay.iconWidth = 100

        return locationOverlay
    }

    override fun onMapReady(p0: NaverMap) {
        this.naverMap = p0
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.NoFollow

        uiSettings()//?????? ui??????

        var locationOverlay = setlocationOverlay()

        var ymarker = Marker()
        ymarker.position = LatLng(37.514,126.838)
        ymarker.map = naverMap
        var zmarker = Marker()
        zmarker.position = LatLng(37.5133,126.83)
        zmarker.map = naverMap

        //?????? ??????????????? ?????????
        naverMap.addOnOptionChangeListener {
            val mode=naverMap.locationTrackingMode
            if(mode== LocationTrackingMode.None){
                naverMap.locationTrackingMode= LocationTrackingMode.NoFollow
            }
            if(mode == LocationTrackingMode.NoFollow) {
                Log.d(TAG, "mode NoFollow")
                naverMap.cameraPosition = CameraPosition(LatLng(lastLocation),16.0, 0.0,0.0)
            }
        }
        //?????? ????????????????????? ?????????
        //bearing????????????????????? ????????? ?????????
        naverMap.addOnLocationChangeListener { location ->
            if(naverMap.locationTrackingMode == LocationTrackingMode.NoFollow){
                locationOverlay.bearing=0f
            }

            //?????? ??? ?????????
            if (isFirst) {
                naverMap.moveCamera(
                    CameraUpdate.scrollAndZoomTo(
                        LatLng(
                            location.latitude,
                            location.longitude
                        ), 16.0
                    )
                )
                isFirst = false
                Log.d(TAG, "????????? ?????? ????????????")
                lastLocation = location
            }

            if (location == lastLocation){//????????????????????????
                Log.d(TAG, "bearing : ${location.bearing}")
            }
            else{//????????????????????????
                if(isStart){//????????? ???????????????
                    //pathOverlay.map=null
                    var updateLocation:LatLng = LatLng(location)
                    walkDistance += updateLocation.distanceTo(pathList.last())//????????? ????????? ?????? ????????? ???????????? ??????
                    walkDistanceTV.text = walkDistance.toInt().toString() + " M"
                    pathList.add(updateLocation)
                    serialPathList.add(SerialLatLng(updateLocation))
                    pathOverlay.coords = pathList
                    pathOverlay.map = naverMap

                    realdb.writeNewUser("x", location.latitude, location.longitude) //?????? ?????? db??????
                    ymarker.position = realdb.getValue("y")
                    ymarker.position = realdb.getValue("z")

                    Log.d("firebase", "ymarker position "+ymarker.position.toString())

                }else {
                    //textView.text = "???????????? 0M"
                }
                Log.d(TAG, "??????????????????")
            }

            lastLocation = location

        }
//        //?????? ????????? ???????????? ????????? ??????
//        naverMap.addOnLoadListener(object:NaverMap.OnLoadListener{
//            override fun onLoad() {
//                var coor:Location? = locationSource.lastLocation
//                if (coor != null) {
//                    Log.d(TAG , "first location ${ coor.latitude }, ${coor.longitude}")
//                    naverMap.moveCamera(CameraUpdate.scrollAndZoomTo(LatLng(coor.latitude,coor.longitude), 16.0))
//                }
//                Log.d(TAG, "????????? ??????")
//            }
//        })
    }
}