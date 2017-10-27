package com.github.kerubistan.kerub.services

import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import org.apache.shiro.authz.annotation.RequiresAuthentication
import java.util.UUID
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/vm-dyn")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiresAuthentication
interface VirtualMachineDynamicService : DynamicService<VirtualMachineDynamic> {
	@Path("{id}/connection/spice")
	@Produces("application/x-virt-viewer")
	@GET
	fun spiceConnection(@PathParam("id") id: UUID): String

}