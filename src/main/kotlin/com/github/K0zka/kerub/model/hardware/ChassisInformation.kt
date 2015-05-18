package com.github.K0zka.kerub.model.hardware

data class ChassisInformation (
		val manufacturer : String,
        val type : String,
        val height : Int?,
        val nrOfPowerCords : Int
                              )