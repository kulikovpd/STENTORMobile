package com.itmotors.stentormobile


data class STENTORCommand(val startLeft : UByte = 1u,
                          val startRight : UByte = 2u,
                          val startBoth : UByte = 3u,

                          val stop : UByte = 4u,
                          val store : UByte = 17u,
                          val oval : UByte = 18u,

                          val end : UByte = 16u,
                          val changeBreakSystem : UByte = 32u,
                          val changeVehicleType : UByte = 33u,

                          val axleUp : UByte = 34u,
                          val vehicleNumberNext : UByte = 36u,
                          val showIndicatorNext : UByte = 38u,

                          val axleDown : UByte = 35u,
                          val vehicleNumberPrevious : UByte = 37u,
                          val showIndicatorPrevious : UByte = 39u)