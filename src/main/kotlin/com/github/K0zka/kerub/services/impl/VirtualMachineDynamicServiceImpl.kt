package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.services.VirtualMachineDynamicService

class VirtualMachineDynamicServiceImpl(dao: VirtualMachineDynamicDao)
: AbstractDynamicServiceImpl<VirtualMachineDynamic>(dao, "vm-dynamic"),
		VirtualMachineDynamicService