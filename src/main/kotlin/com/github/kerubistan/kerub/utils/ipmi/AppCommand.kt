package com.github.kerubistan.kerub.utils.ipmi


enum class AppCommand(val code: Int) {
	GetDeviceId(0x01),
	ColdReset(0x02),
	WarmReset(0x03),
	GetSelfTestResults(0x04),
	ManufacturingTestOn(0x05),
	SetAcpiPowerState(0x06),
	GetAcpiPowerState(0x07),
	GetDeviceGuid(0x08),
	//...
	GetSessionChallenge(0x39),
	ActivateSession(0x3A),
	SetSessionPrivilegeLevel(0x3B),
	CloseSession(0x3C),
	GetSessionInfo(0x3D),
	GetAuthCode(0x3F);

	companion object {
		private val commandsByCode = AppCommand.values().map { it.code to it }.toMap()
		fun getByCode(code: Int): AppCommand =
				commandsByCode[code] ?: throw IllegalArgumentException("Code not known: $code")
	}
}