package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import org.infinispan.Cache
import java.util.UUID

class VirtualStorageDeviceDynamicDaoImpl(cache: Cache<UUID, VirtualStorageDeviceDynamic>, eventListener: EventListener)
: IspnDaoBase<VirtualStorageDeviceDynamic, UUID>(cache, eventListener), VirtualStorageDeviceDynamicDao