package com.itmotors.stentormobile

import de.m3y.kformat.Table
import de.m3y.kformat.table
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.round

@OptIn(ExperimentalUnsignedTypes::class)
data class AxleData(var axle: Int,

                    var weightLeft: Int,
                    var weightRight: Int,

                    var rollResLeft: Int,
                    var rollResRight: Int,

                    var ovalLeft: Int,
                    var ovalRight: Int,

                    var brakingForceLeft: Int,
                    var brakingForceRight: Int,

                    var pedalEffortLeft: Int,
                    var pedalEffortRight: Int,

                    var parkingBrakeForceLeft: Int,
                    var parkingBrakeForceRight: Int,

                    var hasParkingBrake: Boolean = true,

                    var divider: Int
):Serializable {
    companion object {
        operator fun invoke(data: UByteArray, axle: Int, divider: Int): AxleData{
            val weightLeft = Wrapper.wrapInt(0u, 0u, data[3], data[4])
            val weightRight = Wrapper.wrapInt(0u, 0u, data[5], data[6])

            val rollResLeft = Wrapper.wrapInt(0u, 0u, data[11], data[12])
            val rollResRight = Wrapper.wrapInt(0u, 0u, data[13], data[14])

            val ovalLeft = Wrapper.wrapInt(0u, 0u, data[15], data[16])
            val ovalRight = Wrapper.wrapInt(0u, 0u, data[17], data[18])

            val parkingBrakeForceLeft = Wrapper.wrapInt(0u, 0u, data[27], data[28])
            val parkingBrakeForceRight = Wrapper.wrapInt(0u, 0u, data[29], data[30])

            val hasParkingBrake = (parkingBrakeForceLeft + parkingBrakeForceRight != 131070)

            val brakingForceLeft = Wrapper.wrapInt(0u, 0u, data[23], data[24])
            val brakingForceRight = Wrapper.wrapInt(0u, 0u, data[25], data[26])

            val pedalEffortLeft = Wrapper.wrapInt(0u, 0u, data[39], data[40])
            val pedalEffortRight = Wrapper.wrapInt(0u, 0u, data[41], data[42])

            return AxleData(axle, weightLeft, weightRight, rollResLeft, rollResRight, ovalLeft, ovalRight, brakingForceLeft, brakingForceRight, pedalEffortLeft, pedalEffortRight, parkingBrakeForceLeft, parkingBrakeForceRight, hasParkingBrake, divider)
        }
    }

    override fun toString(): String {
        return table{
            header("ОСЬ $axle", "Левое", "Правое", "Сумма", "Неравн.")
            row("Вес оси, kN", weightLeft.toFloat() / divider, weightRight.toFloat() / divider, (weightLeft + weightRight).toFloat() / divider)
            if (max(rollResLeft, rollResRight) != 0)
                row("Сопр. качению, kN", rollResLeft.toFloat() / divider, rollResRight.toFloat() / divider, (rollResLeft + rollResRight).toFloat() / divider, round(abs(rollResLeft - rollResRight).toFloat() / max(rollResLeft, rollResRight) * 100).toInt())
            else row("Сопр. качению, kN", rollResLeft.toFloat() / divider, rollResRight.toFloat() / divider, (rollResLeft + rollResRight).toFloat() / divider, 0)
            if (ovalLeft + ovalRight != 131070) row("Овальность, kN", ovalLeft.toFloat() / divider, ovalRight.toFloat() / divider, (ovalLeft + ovalRight).toFloat() / divider)
            else row("Овальность, kN" )
            if (max(brakingForceLeft, brakingForceRight) != 0)
                row("Тормозная сила, kN", brakingForceLeft.toFloat() / divider, brakingForceRight.toFloat() / divider, (brakingForceLeft + brakingForceRight).toFloat() / divider, round(abs(brakingForceLeft - brakingForceRight).toFloat() / max(brakingForceLeft, brakingForceRight) * 100).toInt())
            else row("Тормозная сила, kN", brakingForceLeft.toFloat() / divider, brakingForceRight.toFloat() / divider, (brakingForceLeft + brakingForceRight).toFloat() / divider, 0)
            row("Усилие на педали, kN", pedalEffortLeft.toFloat() / divider, pedalEffortRight.toFloat() / divider, (pedalEffortLeft + pedalEffortRight).toFloat() / divider)

            hints {
                alignment("ОСЬ $axle", Table.Hints.Alignment.LEFT)
                precision("Левое", 2)
                precision("Правое", 2)
                precision("Сумма", 2)
                postfix("Неравн.", "%")
                borderStyle =  Table.BorderStyle.NONE // or SINGLE_LINE
            }

        }.render(StringBuilder()).toString() + "\n"
    }
}
