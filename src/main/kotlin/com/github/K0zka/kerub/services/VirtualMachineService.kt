package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.VirtualMachine
import java.util.UUID
import javax.ws.rs.Path
import com.wordnik.swagger.annotations.Api

Api("s/r/vm", description = "Virtual machine operations")
Path("/vm")
public interface VirtualMachineService : RestCrud<VirtualMachine>, RestOperations.List<VirtualMachine> {
	//specific VM operations

}