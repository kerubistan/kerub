package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.VirtualMachine
import com.wordnik.swagger.annotations.Api
import java.util.UUID
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam

@Api("s/r/vm", description = "Virtual machine operations")
@Path("/vm")
public interface VirtualMachineService : RestCrud<VirtualMachine>, RestOperations.List<VirtualMachine> {
	@Path("{id}/start")
	@POST
	fun startVm(@PathParam("id") id : UUID)

	@Path("{id}/stop")
	@POST
	fun stopVm(@PathParam("id") id : UUID)
}