package com.itmotors.stentormobile

@OptIn(ExperimentalUnsignedTypes::class)
class CRC16
{
    companion object{
        private fun crc_MODBUS(buffer: UByteArray) : Int{
            var wCRCin = 0xffff
            val polynomial = 0xa001
            for (i in 0..buffer.size - 3) {
                wCRCin = wCRCin xor (buffer[i].toInt() and 0x00ff)
                for (j in 0..7) {
                    if (wCRCin and 0x0001 != 0) {
                        wCRCin = wCRCin shr 1
                        wCRCin = wCRCin xor polynomial
                    } else {
                        wCRCin = wCRCin shr 1
                    }
                }
            }
            return 0x0000.let { wCRCin = wCRCin xor it; wCRCin }
        }

        fun writeCRC(buffer: UByteArray) {
            val crc = crc_MODBUS(buffer)
            buffer[buffer.size - 2] = getByteRepresentation(crc)[0]
            buffer[buffer.size - 1] = getByteRepresentation(crc)[1]
        }

        fun verifyCRC(buffer: UByteArray): Boolean {
            val noCRCbuffer = buffer.copyOfRange(0, buffer.size - 3)
            val correctCRC = crc_MODBUS(noCRCbuffer)
            return buffer[buffer.size - 2] == getByteRepresentation(correctCRC)[0] &&
                    buffer[buffer.size - 1] == getByteRepresentation(correctCRC)[1]
        }

        private fun getByteRepresentation(crc: Int) : UByteArray{
            return ubyteArrayOf( (crc and 0xFF).toUByte(), (crc shr 8).toUByte())
        }
    }
}


