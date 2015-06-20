package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.services.VirtualMachineService

public class VirtualMachineServiceImpl(dao: VirtualMachineDao) : ListableBaseService<VirtualMachine>(dao, "vm"),
		VirtualMachineService {
}