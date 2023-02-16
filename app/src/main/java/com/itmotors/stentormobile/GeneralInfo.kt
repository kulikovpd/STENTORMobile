package com.itmotors.stentormobile

import java.io.Serializable


@OptIn(ExperimentalUnsignedTypes::class)
data class GeneralInfo(var date: String,
                       var stentorID: String,
                       var progVersion: String,
                       var checkDate: String,
                       var vehicleType: String,
                       var testNumber: String,
                       var testTime: String,
                       var divider: Int = 100,
                       var numOfCar: String = "",
                       var vin: String = "",
                       var numOfAxles: Int = 0,
                       var numOfParkingAxles: Int = 0
): Serializable{
    companion object{
        operator fun invoke(data: UByteArray): GeneralInfo{
            val stentorID = Wrapper.wrapInt(data[3], data[4], data[5], data[6]).toUInt().toString()

            val progVersion = Wrapper.wrapShort(data[7], data[8]).toString()

            var day = Wrapper.wrapShort(data[9], data[10]).toString()
            if (day.length == 1) day = "0$day"

            var month = Wrapper.wrapShort(data[11], data[12]).toString()
            if (month.length == 1) month = "0$month"

            var year = Wrapper.wrapShort(data[13], data[14]).toString()
            val checkDate = "$day.$month.$year"

            val testNumber = Wrapper.wrapInt(data[19], data[20], data[21], data[22]).toUInt().toString()

            day = Wrapper.wrapShort(data[23], data[24]).toString()
            if (day.length == 1) day = "0$day"

            month = Wrapper.wrapShort(data[25], data[26]).toString()
            if (month.length == 1) month = "0$month"

            year = Wrapper.wrapShort(data[27], data[28]).toString()

            val date = "$day.$month.$year"

            var hours = Wrapper.wrapShort(data[33], data[34]).toString()
            if (hours.length == 1) hours = "0$hours"

            var minutes = Wrapper.wrapShort(data[35], data[36]).toString()
            if (minutes.length == 1) minutes = "0$minutes"

            val testTime = "$hours:$minutes"

//          val numOfAxles = Wrapper.wrapShort(data[47], data[48]).toString()
            val numOfAxles = data[48].toInt()

            var divider = 100
            if (Wrapper.wrapInt(0u, 0u, data[49], data[50]) == 0)
                divider =  10000

            val vehicleType = if (Wrapper.wrapInt(0u, 0u, data[51], data[52]) == 0){
                "легковой"
            } else{
                "грузовой"
            }

            return GeneralInfo(date, stentorID, progVersion, checkDate, vehicleType, testNumber,
                testTime, divider, "", "", numOfAxles, 0)
        }
    }

    override fun toString(): String {
        return "Дата: " + date + "\n" +
               "Время: " + testTime + "\n" +
               "Версия ПО: " + progVersion[0] + "." + progVersion[1] + progVersion[2] + "\n" +
               "ID стенда: " + stentorID + "\n" +
               "Дата поверки: " + checkDate + "\n" +
               "Порядковый номер испытания: " + testNumber + "\n"  +
               "Тип ТС: " + vehicleType + "\n" +
               "Гос. номер ТС: " + numOfCar + "\n" +
               "VIN: " + vin + "\n"
    }
}
