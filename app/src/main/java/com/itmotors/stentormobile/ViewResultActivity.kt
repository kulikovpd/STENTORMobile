package com.itmotors.stentormobile


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.itmotors.stentormobile.Wrapper.Companion.getSerializable
import java.io.File
import java.io.FileOutputStream



class ViewResultActivity : AppCompatActivity() {

    private lateinit var testInfo: TextView
    private lateinit var num: TextView
    private lateinit var vin: TextView

    private lateinit var file: File

    private lateinit var generalInfo: GeneralInfo
    private lateinit var axleArray: ArrayList<AxleData>
    private lateinit var parkingBrakeDataArray: ArrayList<ParkingBrakeData>
    private lateinit var vehicleTest: VehicleTest

    private lateinit var filePath: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_result)
        testInfo = findViewById(R.id.doc_text)
        num = findViewById(R.id.auto_number_input)
        vin = findViewById(R.id.vin_code_input)
        num.filters += InputFilter.AllCaps()
        vin.filters += InputFilter.AllCaps()
        generalInfo = getSerializable(this, "generalInfo", GeneralInfo::class.java)
        axleArray = arrayListOf()
        parkingBrakeDataArray = arrayListOf()

        if (generalInfo.numOfAxles > 0)
            for (i in 1..generalInfo.numOfAxles){
                axleArray.add(getSerializable(this, "axle$i", AxleData::class.java))
            }

        if (generalInfo.numOfParkingAxles > 0)
            for (i in 1..generalInfo.numOfParkingAxles){
                parkingBrakeDataArray.add(getSerializable(this, "parkingBrakeAxle$i", ParkingBrakeData::class.java))
            }

        vehicleTest = VehicleTest(generalInfo, axleArray, parkingBrakeDataArray)
        testInfo.text = vehicleTest.toSpacedString()
        testInfo.movementMethod = ScrollingMovementMethod()
        testInfo.setHorizontallyScrolling(true)
        num.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                generalInfo.numOfCar = p0.toString()
                testInfo.text = vehicleTest.toSpacedString()
            }

        })

        vin.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                generalInfo.vin = p0.toString()
                testInfo.text = vehicleTest.toSpacedString()
            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.result_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun saveToFile(){
        val mDoc = Document()
        val font = FontFactory.getFont("/res/font/dejavusansmono.ttf", BaseFont.IDENTITY_H, true)
        val mfileName = (vehicleTest.generalInfo.testTime) + "_" +
                vehicleTest.generalInfo.numOfCar + "_" +
                vehicleTest.generalInfo.vin
        var folder = File(getExternalFilesDir(null).toString(), "Отчеты STENTOR")
        if (!(folder.exists() && folder.isDirectory)){
            folder.mkdir()
        }
        folder = File(folder.path, vehicleTest.generalInfo.date)
        if (!(folder.exists() && folder.isDirectory)){
            folder.mkdir()
        }
        filePath = folder.path + "/" + mfileName + ".pdf"
        try{
            PdfWriter.getInstance(mDoc, FileOutputStream(filePath))
            mDoc.open()
            val data = Paragraph(vehicleTest.toString(), font)
            mDoc.add(data)
            mDoc.close()
            Toast.makeText(this, "Сохранено в $filePath", Toast.LENGTH_SHORT).show()
        } catch (e: Exception){
            Toast.makeText(this, "failed:$e", Toast.LENGTH_LONG).show()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_save){
            saveToFile()
        } else if (item.itemId == R.id.id_share){
            saveToFile()
            val uri = FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                File(filePath)
            )
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            sendIntent.putExtra(Intent.EXTRA_STREAM, uri)
            sendIntent.type = "application/pdf"
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(Intent.createChooser(sendIntent, null))
        }
        return super.onOptionsItemSelected(item)
    }
}