package com.github.kerubistan.kerub.services

import com.github.kerubistan.kerub.model.Template
import org.apache.shiro.authz.annotation.RequiresAuthentication
import java.util.UUID
import javax.ws.rs.Consumes
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/template")
@RequiresAuthentication
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
interface TemplateService : AssetService<Template>, RestCrud<Template> {
	@PUT
	@Path("/helpers/from-vm/{vmId}")
	fun buildFromVm(@PathParam("vmId") vmId: UUID) : Template
}
