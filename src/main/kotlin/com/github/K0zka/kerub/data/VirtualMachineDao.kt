package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.VirtualMachine
import java.util.UUID

public trait VirtualMachineDao : CrudDao<VirtualMachine, UUID>, ListableDao<VirtualMachine, UUID> {
}