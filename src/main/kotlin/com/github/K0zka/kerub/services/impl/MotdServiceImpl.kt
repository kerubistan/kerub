package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.services.MotdService
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

public class MotdServiceImpl(var motd : String) : MotdService {
	override
	fun get() : String = motd
}