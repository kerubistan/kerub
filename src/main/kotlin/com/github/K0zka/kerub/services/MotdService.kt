package com.github.K0zka.kerub.services

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

Path("/motd/")
public interface MotdService {
	GET
	Produces(MediaType.TEXT_PLAIN)
	fun get() : String
}