package com.github.kerubistan.kerub.services

import com.github.kerubistan.kerub.model.VirtualMachine
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import org.apache.shiro.authz.annotation.RequiresAuthentication
import java.util.UUID
import javax.ws.rs.Consumes
import javax.ws.rs.GET
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
interface VirtualMachineService
	: RestCrud<VirtualMachine>, RestOperations.List<VirtualMachine>,
		AssetService<VirtualMachine> {

	@ApiOperation(value = "start a virtual machine", notes = "Changes vm-availability expectation to ON on the VM")
	@Path("{id}/start")
	@POST
	fun startVm(@PathParam("id") id: UUID)

	@ApiOperation(value = "stop a virtual machine", notes = "Changes vm-availability expectation to OFF on the VM")
	@Path("{id}/stop")
	@POST
	fun stopVm(@PathParam("id") id: UUID)

	@ApiOperation(value = "list VMs connected to a virtual disk")
	@Path("connected-to/{virtualDiskId}")
	@GET
	fun listByVirtualDisk(@PathParam("virtualDiskId") virtualDiskId: UUID): List<VirtualMachine>

}