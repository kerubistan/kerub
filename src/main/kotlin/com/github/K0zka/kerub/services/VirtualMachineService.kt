package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.VirtualMachine
import com.wordnik.swagger.annotations.Api
import org.apache.shiro.authz.annotation.RequiresAuthentication
import java.util.UUID
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Api("s/r/vm", description = "Virtual machine operations")
@Path("/vm")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiresAuthentication
public interface VirtualMachineService : RestCrud<VirtualMachine>, RestOperations.List<VirtualMachine> {
	@Path("{id}/start")
	@POST
	fun startVm(@PathParam("id") id: UUID)

	@Path("{id}/stop")
	@POST
	fun stopVm(@PathParam("id") id: UUID)
}