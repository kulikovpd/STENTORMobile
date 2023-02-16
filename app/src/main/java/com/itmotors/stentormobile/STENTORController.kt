package com.itmotors.stentormobile


@OptIn(ExperimentalUnsignedTypes::class)
class STENTORController {

    companion object{
//        val statusNames = arrayOf(
//            "Стенд свободен",
//            "Готов к измерению",
//            "Двигатели включены, идёт измерение",
//            "Двигатели остановлены, но результаты еще не сохранены",
//            "Результаты сохранены",
//            "Испытание автомобиля завершено"
//        )
        val cmd = STENTORCommand()

        private var readRequest = byteArrayOf(
            0x01,  //адрес устройства
            0x04,  //код команды "чтение регистров входных данных"
            0x04, 0xB0.toUByte().toByte(),  //адрес первого регистра = 1200
            0x00, 0x14,  //кол-во считываемых регистров = 20
            0x00, 0x00 //checksum = вычисляется в конструкторе
        ).toUByteArray()

        private val writeRequest = byteArrayOf(
            0x01,  //адрес устройства
            0x10,  //код команды "запись в регистр хранения"
            0x00, 0x00,  //адрес регстра хранения = 0
            0x00, 0x01,  //кол-во записываемых регистров = 1
            0x00, 0x02,  //кол-во байт данных
            0x00, 0x00,  //данные для записи = номер команды STENTOR
            0x00, 0x00 //checksum = вычисляется перед отправкой
        ).toUByteArray()

        fun makeWriteRequest(command: UByte) : ByteArray {
            val temp = writeRequest
            temp[8] = command
            CRC16.writeCRC(temp)
            return temp.toByteArray()
        }

        fun makeReadRequest(firstRegister: Int = 1200,
                            numOfRegisters: Int = 20) : ByteArray{
            val temp = readRequest
            temp[2] = (firstRegister / 256).toUByte()
            temp[3] = (firstRegister - 256 * temp[2].toInt()).toUByte()
            temp[5] = numOfRegisters.toUByte()
            CRC16.writeCRC(temp)
            return temp.toByteArray()
        }
    }




}