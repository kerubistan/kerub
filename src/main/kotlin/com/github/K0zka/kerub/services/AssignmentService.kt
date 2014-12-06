package com.github.K0zka.kerub.services

import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.Consumes
import com.github.K0zka.kerub.model.controller.Assignment
import javax.ws.rs.PathParam

Path("/controllers/")
Produces(MediaType.APPLICATION_JSON)
Consumes(MediaType.APPLICATION_JSON)
trait AssignmentService {
	Path("{controller}/assignments")
	fun listByController(
			PathParam("controller")
			controller : String) : List<Assignment>
}