package com.itmotors.stentormobile

import de.m3y.kformat.Table
import de.m3y.kformat.table
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.round

data class ParkingBrakeData(var axle: Int,
                            var parkingBrakeForceLeft: Int,
                            var parkingBrakeForceRight: Int,
                            private var divider: Int
): Serializable{
    companion object{
        operator fun invoke(axleTest: AxleData): ParkingBrakeData{
            return ParkingBrakeData(axleTest.axle, axleTest.parkingBrakeForceLeft, axleTest.parkingBrakeForceRight, axleTest.divider)
        }
    }

    override fun toString(): String {
        return table{
            header("ОСЬ $axle", "Левое", "Правое", "Сумма", "Неравн.")
            if (max(parkingBrakeForceLeft, parkingBrakeForceRight) != 0)
                row("Тормозная сила, kN  ", parkingBrakeForceLeft.toFloat() / divider, parkingBrakeForceRight.toFloat() / divider, (parkingBrakeForceLeft + parkingBrakeForceRight).toFloat() / divider, round(abs(parkingBrakeForceLeft - parkingBrakeForceRight).toFloat() / max(parkingBrakeForceLeft, parkingBrakeForceRight) * 100).toInt())
            else
                row("Тормозная сила, kN  ", parkingBrakeForceLeft.toFloat() / divider, parkingBrakeForceRight.toFloat() / divider, (parkingBrakeForceLeft + parkingBrakeForceRight).toFloat() / divider, 0)
            hints {
                alignment("ОСЬ $axle", Table.Hints.Alignment.LEFT)
                precision("Левое", 2)
                precision("Правое", 2)
                precision("Сумма", 2)
                postfix("Неравн.", "%")
                borderStyle = Table.BorderStyle.NONE // or SINGLE_LINE
            }
        }.render(StringBuilder()).toString() + "\n"
    }
}
