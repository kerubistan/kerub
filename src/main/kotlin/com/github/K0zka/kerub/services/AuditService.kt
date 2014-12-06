package com.github.K0zka.kerub.services

import javax.ws.rs.Path
import javax.ws.rs.GET
import java.util.UUID
import javax.ws.rs.PathParam
import com.github.K0zka.kerub.model.AuditEntry
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.core.MediaType

Path("/audit")
Produces(MediaType.APPLICATION_JSON)
Consumes(MediaType.APPLICATION_JSON)
public trait AuditService {
	Path("/{id}")
	GET
	fun listById(PathParam("id") id : UUID) : List<AuditEntry>
}