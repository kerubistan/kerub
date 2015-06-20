package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.AuditEntry
import org.apache.shiro.authz.annotation.RequiresAuthentication
import java.util.UUID
import javax.ws.rs.*

Path("/audit")
Produces("application/json")
Consumes("application/json")
RequiresAuthentication
public interface AuditService {
	Path("/{id}")
	GET
	fun listById(PathParam("id") id : UUID) : List<AuditEntry>
}
