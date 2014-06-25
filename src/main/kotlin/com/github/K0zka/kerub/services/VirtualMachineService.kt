package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.VirtualMachine
import java.util.UUID
import javax.ws.rs.Path

Path("/vm")
public trait VirtualMachineService : Listable<VirtualMachine> , RestCrud<VirtualMachine, UUID> {
	//specific VM operations

}