package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.data.dynamic.ControllerDynamicDao
import org.infinispan.manager.EmbeddedCacheManager
import com.github.K0zka.kerub.model.dynamic.ControllerDynamic
import java.net.InetAddress

public class ControllerManagerImpl(val dao : ControllerDynamicDao,
                                   val cacheManager: EmbeddedCacheManager)
: ControllerManager {

	fun start() {
		val address = cacheManager.getAddress().toString()
		dao.add(ControllerDynamic(address, 64, 0, InetAddress.getAllByName("localhost").map { it.toString() }))
	}

}