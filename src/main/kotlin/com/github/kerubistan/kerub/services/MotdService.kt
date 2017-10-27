package com.github.kerubistan.kerub.services

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/motd/") interface MotdService {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	fun get(): String
}