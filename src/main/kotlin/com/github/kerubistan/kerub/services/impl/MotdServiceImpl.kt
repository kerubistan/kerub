package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.services.MotdService

class MotdServiceImpl(var motd: String) : MotdService {
	override
	fun get(): String = motd
}