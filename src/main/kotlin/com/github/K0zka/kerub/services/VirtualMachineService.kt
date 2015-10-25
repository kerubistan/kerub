package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.VirtualMachine
import com.wordnik.swagger.annotations.Api
import javax.ws.rs.Path

@Api("s/r/vm", description = "Virtual machine operations")
@Path("/vm")
public interface VirtualMachineService : RestCrud<VirtualMachine>, RestOperations.List<VirtualMachine> {
	//specific VM operations

}