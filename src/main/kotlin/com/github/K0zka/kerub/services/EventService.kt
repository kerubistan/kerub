package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.Event
import java.util.UUID
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

Path("/event")
Produces(MediaType.APPLICATION_JSON)
Consumes(MediaType.APPLICATION_JSON)
public interface EventService {
	GET
	Path("/{id}")
	fun getById(PathParam("id") id : UUID) : Event
	GET
	Path("/")
	fun list() : List<Event>
}