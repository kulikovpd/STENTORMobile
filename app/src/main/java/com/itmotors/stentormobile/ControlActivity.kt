package com.itmotors.stentormobile

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.itmotors.stentormobile.databinding.ActivityControlBinding
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@OptIn(ExperimentalUnsignedTypes::class)
class ControlActivity : AppCompatActivity(), ReceiveThread.Listener, EasyPermissions.PermissionCallbacks{
    private lateinit var binding: ActivityControlBinding
    private lateinit var actListLauncher: ActivityResultLauncher<Intent>
    private lateinit var btConnection: BtConnection
    private var listItem: ListItem? = null
    private var workMode: Boolean = false
    private lateinit var workThread: WorkMode
    private lateinit var vehicleTestExample: VehicleTest
    private lateinit var parsedData: DataParser

    private lateinit var axelNumber: TextView

    private lateinit var progressBarLeft: ProgressBar
    private lateinit var progressBarRight: ProgressBar

    private lateinit var leftValueLabel: TextView
    private lateinit var rightValueLabel: TextView

    private lateinit var leftValue: TextView
    private lateinit var rightValue: TextView

    private lateinit var breakSystemType: TextView
    private lateinit var vehicleType: TextView

    private lateinit var leftUpArrow: ImageView
    private lateinit var rightUpArrow: ImageView
    private lateinit var leftDownArrow: ImageView
    private lateinit var rightDownArrow: ImageView


    private lateinit var message: TextView

    private lateinit var connectState: TextView


    private fun setArrowOn(arrow: ImageView){
        arrow.setImageResource(R.drawable.blicking_triangle)
    }

    private fun setArrowOff(arrow: ImageView){
        arrow.setImageResource(R.drawable.triangle)
    }

    private fun setArrow(arrow: ImageView, status: Boolean, isBlinking: Boolean){
        runOnUiThread {
            if (status) setArrowOn(arrow)
            else setArrowOff(arrow)


            if (isBlinking) arrow.blink()
            else arrow.stopBlinking()
        }
    }

    private fun setLabel(label: TextView, text: String, isBlinking: Boolean){
        runOnUiThread{
            label.text = text
            if (isBlinking) label.blink()
            else label.stopBlinking()
        }
    }

    private fun View.blink(
        times: Int = Animation.INFINITE,
        duration: Long = 50L,
        offset: Long = 20L,
        minAlpha: Float = 0.0f,
        maxAlpha: Float = 1.0f,
        repeatMode: Int = Animation.REVERSE
    ) {
        startAnimation(AlphaAnimation(minAlpha, maxAlpha).also {
            it.duration = duration
            it.startOffset = offset
            it.repeatMode = repeatMode
            it.repeatCount = times
        })
    }

    private fun ImageView.blink(
        times: Int = Animation.INFINITE,
        duration: Long = 50L,
        offset: Long = 20L,
        minAlpha: Float = 0.0f,
        maxAlpha: Float = 1.0f,
        repeatMode: Int = Animation.REVERSE
    ){
        startAnimation(AlphaAnimation(minAlpha, maxAlpha).also {
            it.duration = duration
            it.startOffset = offset
            it.repeatMode = repeatMode
            it.repeatCount = times
        })
    }

    private fun ImageView.stopBlinking(){
        clearAnimation()
    }

    private fun View.stopBlinking(){
        clearAnimation()
    }

    private fun toggleButtons(state: Boolean){
        binding.apply {
            startLeft.isEnabled = state
            startRight.isEnabled = state
            start.isEnabled = state
            elipse.isEnabled = state
            systemSelection.isEnabled = state
            save.isEnabled = state
            car.isEnabled = state
            stop.isEnabled = state
            end.isEnabled = state
            axisUp.isEnabled = state
            axisDown.isEnabled = state
            numUp.isEnabled = state
            numDown.isEnabled = state
            indUp.isEnabled = state
            indDown.isEnabled = state
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission()
        binding = ActivityControlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBtListResult()
        init()
        workMode = false
        toggleButtons(workMode)
        vehicleTestExample = VehicleTest()
        binding.apply {
            startLeft.setOnClickListener {
                workThread.putCommand(STENTORController.cmd.startLeft)
            }
            start.setOnClickListener {
                workThread.putCommand(STENTORController.cmd.startBoth)
            }
            startRight.setOnClickListener {
                workThread.putCommand(STENTORController.cmd.startRight)
            }
            elipse.setOnClickListener {
                workThread.putCommand(STENTORController.cmd.oval)
            }
            systemSelection.setOnClickListener {
                workThread.putCommand(STENTORController.cmd.changeBreakSystem)
            }
            save.setOnClickListener {
                workThread.putCommand(STENTORController.cmd.store)
            }
            car.setOnClickListener {
                workThread.putCommand(STENTORController.cmd.changeVehicleType)
            }
            stop.setOnClickListener {
                workThread.putCommand(STENTORController.cmd.stop)
            }
            end.setOnClickListener {
//                val testInfo = GeneralInfo("09.09.2022",
//                    "stentorID",
//                    "100",
//                    "01.01.2011",
//                    "легковой",
//                    "1",
//                    "12:22",
//                    100,
//                    "",
//                    "",
//                    2,
//                    1)
//                val axle1 = AxleData(1,
//                    435,
//                    440,
//                    210,
//                    230,
//                    65535,
//                    65535,
//                    399,
//                    383,
//                    0,
//                    0,
//                    65535,
//                    65535,
//                    false,
//                    100
//                )
//                val axle2 = AxleData(2,
//                    425,
//                    450,
//                    220,
//                    240,
//                    65535,
//                    65535,
//                    359,
//                    356,
//                    0,
//                    0,
//                    358,
//                    356,
//                    true,
//                    100
//                )
//                val pAxle = ParkingBrakeData(2,
//                    358, 356, 100)
//                val intent = Intent(applicationContext, ViewResultActivity::class.java)
//                intent.putExtra("generalInfo", testInfo)
//                intent.putExtra("axle1", axle1)
//                intent.putExtra("axle2", axle2)
//                intent.putExtra("parkingBrakeAxle1", pAxle)
//                startActivity(intent)
                    Log.d("CA", "trying to end test")
                    workThread.putCommand(STENTORController.cmd.end)
                    Thread.sleep(200)
                    runOnUiThread {
                        toggleWorkMode(false)
                    }
                    Log.d("CA", "workmode stopped")
                    Thread.sleep(200)


                    Log.d("CA", "trying to read general info")
                    btConnection.sendData(STENTORController.makeReadRequest(1000, 25))
                    Thread.sleep(200)
                    if (vehicleTestExample.generalInfo.numOfAxles > 0){
                        for (i in 1..vehicleTestExample.generalInfo.numOfAxles){
                            btConnection.sendData(STENTORController.makeReadRequest(100 * i, 21))
                            Log.d("CA", "trying to get axle info")
                            Thread.sleep(200)
                        }
                    }

                    val testInfo = vehicleTestExample.generalInfo
                    Log.d("CA", "axlesNumber${vehicleTestExample.generalInfo.numOfAxles}")
                    val intent = Intent(applicationContext, ViewResultActivity::class.java)
                    intent.putExtra("generalInfo", testInfo)

                    if (vehicleTestExample.generalInfo.numOfAxles > 0){
                        for (i in 1..testInfo.numOfAxles){
                            Log.d("CA", "put axle info")
                            intent.putExtra("axle$i", vehicleTestExample.axleArray[i - 1])
                        }
                    }

                    if (vehicleTestExample.generalInfo.numOfParkingAxles > 0){
                        for (i in 1..vehicleTestExample.generalInfo.numOfParkingAxles)
                            intent.putExtra("parkingBrakeAxle$i", vehicleTestExample.parkingBrakeDataArray[i - 1])
                    }

                    startActivity(intent)

                    vehicleTestExample = VehicleTest()

                    runOnUiThread{
                        toggleWorkMode(true)
                    }
            }
            axisUp.setOnClickListener {
                workThread.putCommand(STENTORController.cmd.axleUp)
            }
            axisDown.setOnClickListener {
                workThread.putCommand(STENTORController.cmd.axleDown)
            }
            numUp.setOnClickListener {
                workThread.putCommand(STENTORController.cmd.vehicleNumberNext)
            }
            numDown.setOnClickListener {
                workThread.putCommand(STENTORController.cmd.vehicleNumberPrevious)
            }
            indUp.setOnClickListener {
                workThread.putCommand(STENTORController.cmd.showIndicatorNext)
            }
            indDown.setOnClickListener {
                workThread.putCommand(STENTORController.cmd.showIndicatorPrevious)
            }
        }

    }



    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (TrackingUtility.hasPermissions(this)){
                return
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                    EasyPermissions.requestPermissions(
                        this,
                        getString(R.string.permissions_rationale_message),
                        1,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }else {
                    EasyPermissions.requestPermissions(
                        this,
                        getString(R.string.permissions_rationale_message),
                        1,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            AppSettingsDialog.Builder(this).build().show()
        }
        else{
            requestPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }




    private fun init(){
        val btManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val btAdapter = btManager.adapter
        btConnection = BtConnection(btAdapter, this)
        workThread = WorkMode(btConnection)

        axelNumber = findViewById(R.id.someText)

        progressBarLeft = findViewById(R.id.ringPB)
        progressBarRight = findViewById(R.id.circlePB)



        leftValueLabel = findViewById(R.id.text_testimony_0)
        rightValueLabel = findViewById(R.id.text_testimony_1)


        leftValue = findViewById(R.id.tv_testimony_0)

        rightValue = findViewById(R.id.tv_testimony_1)

        breakSystemType = findViewById(R.id.text_PB_1)
        vehicleType = findViewById(R.id.text_PB_2)

        leftUpArrow = findViewById(R.id.t_up_0)
        rightUpArrow = findViewById(R.id.t_up_1)
        leftDownArrow = findViewById(R.id.tr_down_testimony_0)
        rightDownArrow = findViewById(R.id.tr_down_testimony_1)

        message = findViewById(R.id.remote_message)

        connectState = findViewById(R.id.connectionState)


    }

    private fun toggleWorkMode(state: Boolean)
    {
        workMode = state
        if (state) workThread.start()
        else workThread.stop()
        toggleButtons(state)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.control_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.id_list){
            actListLauncher.launch(Intent(this, BtListActivity::class.java))

        } else {
            if(item.itemId == R.id.id_connect) {
                if (!btConnection.isActive())
                {
                    if (listItem != null){
                        listItem.let {
                            btConnection.connect(it?.mac!!)
                        }
                    }
                    else Toast.makeText(applicationContext, "Выберите стенд в меню выбора устройств", Toast.LENGTH_SHORT).show()
                }
                else Toast.makeText(applicationContext, "Соединение уже установлено", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Suppress("DEPRECATION")
    private fun onBtListResult(){
        actListLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                listItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    it.data?.getSerializableExtra(BtListActivity.DEVICE_KEY, ListItem::class.java)
                }else {
                    it.data?.getSerializableExtra(BtListActivity.DEVICE_KEY) as ListItem
                }
                if (listItem != null){
                    Toast.makeText(applicationContext, "Выбрано устройство Bluetooth:\n${listItem!!.name}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        if (workMode) workThread.stop()
        if (btConnection.isActive()) btConnection.closeConnection()
        super.onDestroy()
    }

    override fun onStringReceive(txt: String, color: String) {
        runOnUiThread {
            if (txt == "rThread started")
            {
                toggleWorkMode(true)
            } else {
                connectState.text = txt
                connectState.setTextColor(Color.parseColor(color))
            }
        }
    }


    override fun onByteReceive(data: UByteArray, size: Int) {
        //Log.d("ControlActivity", "Received array of size $size")
        when (CRC16.verifyCRC(data)){
            true -> {
                when (data[1]){
                    0x04.toUByte() -> {
                        if (workMode){
                            runOnUiThread {
                                parsedData = DataParser()
                                parsedData.fillData(data)

                                if (parsedData.leftScaleValue in 0.0..1.0)
                                    progressBarLeft.progress = (parsedData.leftScaleValue * 100).toInt()

                                if (parsedData.rightScaleValue in 0.0..1.0)
                                    progressBarRight.progress = (parsedData.rightScaleValue * 100).toInt()

                                leftValue.text = parsedData.leftDigitValue
                                rightValue.text = parsedData.rightDigitValue

                                setLabel(leftValueLabel, parsedData.leftText, parsedData.leftBlinking)
                                setLabel(rightValueLabel, parsedData.rightText, parsedData.rightBlinking)

                                setArrow(leftUpArrow, parsedData.leftUpArrowStatus, parsedData.leftUpArrowBlinking)
                                setArrow(leftDownArrow, parsedData.leftDownArrowStatus, parsedData.leftDownArrowBlinking)
                                setArrow(rightUpArrow, parsedData.rightUpArrowStatus, parsedData.rightUpArrowBlinking)
                                setArrow(rightDownArrow, parsedData.rightDownArrowStatus, parsedData.rightDownArrowBlinking)

                                setLabel(vehicleType, parsedData.vehicleType, parsedData.vehicleTypeBlinking)
                                setLabel(breakSystemType, parsedData.breakSystemType, parsedData.breakSystemTypeBlinking)

                                axelNumber.text = parsedData.axle

                                message.text = parsedData.message
                            }
                        }
                        else{
                            Log.d("CA", "workthread paused, size received$size")
                            if (size == 55){
                                Log.d("CA", "get print info")
                                vehicleTestExample.setGeneralInfo(data)
                            }
                            else if (size == 47){
                                Log.d("CA", "get axle info")
                                vehicleTestExample.addAxle(data)
                            }
                        }
                    }
                    0x10.toUByte() -> {
                        //Log.d("ControlActivity", "Received SEND response")
                        workThread.putCommand(0u)
                    }
                    else -> {

                    }
                }
            }
            else -> {
                Log.d("ControlActivity", "Wrong CRC")
            }
        }
        if (workMode) workThread.resumeSending()
    }
}