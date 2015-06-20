package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.data.dynamic.ControllerDynamicDao
import com.github.K0zka.kerub.model.dynamic.ControllerDynamic
import com.github.K0zka.kerub.utils.getLogger
import org.infinispan.manager.EmbeddedCacheManager
import java.net.InetAddress

public class ControllerManagerImpl(val dao: ControllerDynamicDao,
                                   val cacheManager: EmbeddedCacheManager)
: ControllerManager {
	override fun getControllerId(): String = cacheManager.getAddress().toString()

	companion object {
		val logger = getLogger(ControllerManagerImpl::class)
	}

	fun start() {
		val address = getControllerId()
		logger.info("Cache address: {}", address)
		dao.add(ControllerDynamic(address, 64, 0, InetAddress.getAllByName("localhost").map { it.toString() }))
	}

}