package com.itmotors.stentormobile

@OptIn(ExperimentalUnsignedTypes::class)
class DataParser {

    //var status : Int = 0

    var leftScaleValue: Double = 0.0
    var rightScaleValue: Double = 0.0

    lateinit var leftDigitValue : String
    lateinit var rightDigitValue: String

    lateinit var leftText: String
    lateinit var rightText: String

    var leftBlinking: Boolean = false
    var rightBlinking: Boolean = false

    var leftUpArrowStatus: Boolean = false
    var leftDownArrowStatus: Boolean = false
    var rightUpArrowStatus: Boolean = false
    var rightDownArrowStatus: Boolean = false

    var leftUpArrowBlinking: Boolean = false
    var leftDownArrowBlinking: Boolean = false
    var rightUpArrowBlinking: Boolean = false
    var rightDownArrowBlinking: Boolean = false

    lateinit var axle : String

    lateinit var vehicleType: String
    var vehicleTypeBlinking: Boolean = false

    lateinit var breakSystemType: String
    var breakSystemTypeBlinking: Boolean = false


    lateinit var message: String

    fun fillData(data: UByteArray){
        //status = Wrapper.wrapInt(0u, 0u, data[3], data[4])


        leftScaleValue = Wrapper.wrapInt(0u, 0u, data[7], data[8]).toDouble() / 1000.0

        rightScaleValue = Wrapper.wrapInt(0u, 0u, data[9], data[10]).toDouble() / 1000.0


        val point = Wrapper.wrapInt(0u, 0u, data[19], data[20])

        leftDigitValue = setDigitValue(Wrapper.wrapShort(data[11], data[12]), point)

        rightDigitValue = setDigitValue(Wrapper.wrapShort(data[13], data[14]), point)


        if (data[16] and 0x01.toUByte() != 0.toUByte()) {
            leftText = "тормозная сила"
            leftBlinking = data[15] and 0x80.toUByte() != 0.toUByte()
        } else if ((data[16] and 0x02.toUByte()) != 0.toUByte()) {
            leftText = "овальность"
            leftBlinking = data[15] and 0x40.toUByte() != 0.toUByte()
        } else {
            leftText = "---"
            leftBlinking = false
        }

        if (data[18] and 0x01.toUByte() != 0.toUByte()) {
            rightText = "тормозная сила"
            rightBlinking = data[17] and 0x80.toUByte() != 0.toUByte()
        } else if ((data[18] and 0x02.toUByte()) != 0.toUByte()) {
            rightText = "овальность"
            rightBlinking = data[17] and 0x40.toUByte() != 0.toUByte()
        } else {
            rightText = "---"
            rightBlinking = false
        }


        leftUpArrowStatus = data[16] and 0x04.toUByte() != 0.toUByte()
        leftDownArrowStatus = data[16] and 0x08.toUByte() != 0.toUByte()
        rightUpArrowStatus = data[18] and 0x04.toUByte() != 0.toUByte()
        rightDownArrowStatus = data[18] and 0x08.toUByte() != 0.toUByte()

        leftUpArrowBlinking = data[15] and 0x04.toUByte() != 0.toUByte()
        leftDownArrowBlinking = data[15] and 0x08.toUByte() != 0.toUByte()
        rightUpArrowBlinking = data[17] and 0x04.toUByte() != 0.toUByte()
        rightDownArrowBlinking = data[17] and 0x08.toUByte() != 0.toUByte()


        if ((data[18] and 0x40.toUByte()) != 0.toUByte()) {
            vehicleType = "авария"
            vehicleTypeBlinking = data[17] and 0x40.toUByte() != 0.toUByte()
        } else if ((data[18] and 0x10.toUByte()) != 0.toUByte()) {
            vehicleType = "легковой"
            vehicleTypeBlinking = data[17] and 0x10.toUByte() != 0.toUByte()
        } else if ((data[18] and 0x20.toUByte()) != 0.toUByte()) {
            vehicleType = "грузовой"
            vehicleTypeBlinking = data[17] and 0x20.toUByte() != 0.toUByte()
        } else {
            vehicleType = "---"
            vehicleTypeBlinking = false
        }


        if ((data[16] and 0x10.toUByte()) != 0.toUByte()) {
            breakSystemType = "рабочая"
            breakSystemTypeBlinking = data[15] and 0x08.toUByte() != 0.toUByte()
        } else if ((data[16] and 0x20.toUByte()) != 0.toUByte()) {
            breakSystemType = "стояночная"
            breakSystemTypeBlinking = data[15] and 0x04.toUByte() != 0.toUByte()
        } else if ((data[16] and 0x40.toUByte()) != 0.toUByte()) {
            breakSystemType = "запасная"
            breakSystemTypeBlinking = data[15] and 0x02.toUByte() != 0.toUByte()
        } else {
            breakSystemType = "---"
            breakSystemTypeBlinking = false
        }

        axle = "ОСЬ " + Wrapper.wrapShort(data[21], data[22])

        message = getMessage(data)

    }

    private fun getMessage(arr: UByteArray) : String {
        val tmp = ByteArray(20)
        for ((counter, i) in (23..42).withIndex()){
            tmp[counter] = arr[i].toByte()

        }
        return String(tmp, charset("Windows-1251"))
    }

    private fun setDigitValue(value: Short, point: Int): String{
        if (value < -999) {
            return "‾ ‾ ‾ ‾"
        } else if (value > 9999) {
            return "_ _ _ _"
        } else if (value == 0.toShort()) {
            return "0.00"
        } else {
            var res = value.toString()
            return if (point > 0){
                while (res.length <= point){
                    res += "0$res"
                }
                res.subSequence(0, res.length - point).toString() + "." +
                res.subSequence(res.length - point, res.length).toString()
            } else res
        }
    }



}