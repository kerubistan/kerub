package com.github.K0zka.kerub.services

import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import com.github.K0zka.kerub.model.Event
import javax.ws.rs.GET
import java.util.UUID
import javax.ws.rs.PathParam

Path("/event")
Produces("application/json")
Consumes("application/json")
public trait EventService {
	GET
	Path("/{id}")
	fun getById(PathParam("id") id : UUID) : Event
	GET
	Path("/")
	fun list() : List<Event>
}