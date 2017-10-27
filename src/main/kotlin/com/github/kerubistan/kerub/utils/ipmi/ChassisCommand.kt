package com.github.kerubistan.kerub.utils.ipmi

//see ipmi 2.0 specification page 614
enum class ChassisCommand(val code: Int) {
	GetChassisCapabilities(0x00),
	GetChassisStatus(0x01),
	ChassisControl(0x02),
	ChassisReset(0x03),
	ChassisIdentify(0x04),

}