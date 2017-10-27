package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import org.infinispan.Cache
import java.util.UUID

class VirtualStorageDeviceDynamicDaoImpl(cache: Cache<UUID, VirtualStorageDeviceDynamic>, eventListener: EventListener)
	: IspnDaoBase<VirtualStorageDeviceDynamic, UUID>(cache, eventListener), VirtualStorageDeviceDynamicDao