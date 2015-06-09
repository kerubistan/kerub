package com.github.K0zka.kerub.services

import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.Consumes
import com.github.K0zka.kerub.data.ControllerDao
import javax.ws.rs.GET
import javax.ws.rs.Path

Produces(MediaType.APPLICATION_JSON)
Consumes(MediaType.APPLICATION_JSON)
Path("/controllers/")
public interface ControllerService {
	GET
	Path("/")
	fun list() : List<String>
}