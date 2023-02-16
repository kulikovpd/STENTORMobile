package com.itmotors.stentormobile

import kotlin.collections.ArrayList

@OptIn(ExperimentalUnsignedTypes::class)
class VehicleTest() {

    constructor(_generalInfo: GeneralInfo, _axleArray: ArrayList<AxleData>, _parkingBrakeDataArray: ArrayList<ParkingBrakeData>) : this() {
        this.generalInfo = _generalInfo
        this.axleArray = _axleArray
        this.parkingBrakeDataArray = _parkingBrakeDataArray
    }

    lateinit var generalInfo: GeneralInfo

    var axleArray: ArrayList<AxleData> = arrayListOf()
    var parkingBrakeDataArray: ArrayList<ParkingBrakeData> = arrayListOf()

    fun setGeneralInfo(data: UByteArray){
        generalInfo = GeneralInfo(data)
    }

    fun addAxle(data: UByteArray){
        val axleTest = AxleData(data, axleArray.size + 1, generalInfo.divider)
        axleArray.add(axleTest)
        if (axleTest.hasParkingBrake) {
            parkingBrakeDataArray.add(ParkingBrakeData(axleTest))
            generalInfo.numOfParkingAxles++
        }
    }

    override fun toString(): String {
        var res = ""
        res += generalInfo.toString()
        if (axleArray.isNotEmpty()){
            res += "Рабочая тормозная система \n"
            for (axle in axleArray){
                res += axle.toString()
            }
        }
        if (parkingBrakeDataArray.isNotEmpty()){
            res += "Стояночная тормозная система \n"
            for (axle in parkingBrakeDataArray){
                res += axle.toString()
            }
        }

        res += "ИТОГО ПО ТС \n"
        var totalWeight = 0.0
        var totalBF = 0.0
        var totalPBF = 0.0
        if (axleArray.isNotEmpty()){
            for (axle in axleArray){
                totalWeight += (axle.weightLeft + axle.weightRight).toFloat() / generalInfo.divider
                totalBF += (axle.brakingForceLeft + axle.brakingForceRight).toFloat() / generalInfo.divider
            }
        }
        if (parkingBrakeDataArray.isNotEmpty()){
            for (axle in parkingBrakeDataArray){
                totalPBF += (axle.parkingBrakeForceLeft + axle.parkingBrakeForceRight).toFloat() / generalInfo.divider
            }
        }
        res += "Вес, kN: "
        res += String.format("%.2f", totalWeight) + "\n"
        res += "Рабочая тормозная система, kN: "
        res += String.format("%.2f", totalBF) + "\n"
        if (parkingBrakeDataArray.isNotEmpty())
        {
            res += "Стояночная тормозная система, kN: "
            res += String.format("%.2f", totalPBF) + "\n"
        }
        res += "\n"
        if (totalWeight != 0.0){
            res += "УДЕЛЬНАЯ ТОРМОЗНАЯ СИЛА \n"
            res += "Рабочая тормозная система: "
            res += String.format("%.2f", totalBF / totalWeight) + "\n"
            if (parkingBrakeDataArray.isNotEmpty()) {
                res += "Стояночная тормозная система: "
                res += String.format("%.2f", totalPBF / totalWeight) + "\n"
            }
        }
        return res
    }

    fun toSpacedString(): String{
        return " " + this.toString().replace("\n", "\n ")
    }
}