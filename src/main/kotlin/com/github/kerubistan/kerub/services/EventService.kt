package com.github.kerubistan.kerub.services

import com.github.kerubistan.kerub.model.Event
import java.util.UUID
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/event")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
interface EventService {
	@GET
	@Path("/{id}")
	fun getById(@PathParam("id") id: UUID): Event

	@GET
	@Path("/")
	fun list(): List<Event>
}