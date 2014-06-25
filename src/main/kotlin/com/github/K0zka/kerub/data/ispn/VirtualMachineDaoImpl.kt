package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.model.VirtualMachine
import java.util.UUID

public class VirtualMachineDaoImpl : VirtualMachineDao, IspnDaoBase<VirtualMachine, UUID>() {
}