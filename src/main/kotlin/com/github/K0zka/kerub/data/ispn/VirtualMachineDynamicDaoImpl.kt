package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import org.infinispan.Cache
import java.util.UUID

class VirtualMachineDynamicDaoImpl(cache: Cache<UUID, VirtualMachineDynamic>,
								   eventListener: EventListener)
: IspnDaoBase<VirtualMachineDynamic, UUID>(cache, eventListener), VirtualMachineDynamicDao