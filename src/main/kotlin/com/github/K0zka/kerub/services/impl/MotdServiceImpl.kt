package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.services.MotdService

public class MotdServiceImpl(var motd: String) : MotdService {
	override
	fun get(): String = motd
}