package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualStorageDevice
import java.util.UUID

interface VirtualMachineDao : ListableCrudDao<VirtualMachine, UUID>, DaoOperations.SimpleSearch<VirtualMachine> {
}