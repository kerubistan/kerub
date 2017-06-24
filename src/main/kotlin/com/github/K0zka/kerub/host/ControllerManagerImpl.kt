package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.data.dynamic.ControllerDynamicDao
import com.github.K0zka.kerub.model.dynamic.ControllerDynamic
import com.github.K0zka.kerub.utils.getLogger
import org.infinispan.manager.EmbeddedCacheManager
import java.io.IOException
import java.net.InetAddress

class ControllerManagerImpl(val dao: ControllerDynamicDao,
							val cacheManager: EmbeddedCacheManager)
	: ControllerManager {

	var id: String? = null

	private fun getHostName(): String? {
		try {
			return InetAddress.getLocalHost().hostName
		} catch(ioe: IOException) {
			return null
		}
	}

	private fun initId() {
		if (id == null) {
			id = getHostName()
		}
	}

	override fun getControllerId(): String = id ?: cacheManager.address.toString()

	companion object {
		val logger = getLogger(ControllerManagerImpl::class)
	}

	fun start() {
		initId()
		val address = getControllerId()
		logger.info("Cache address: {}", address)
		dao.add(ControllerDynamic(address, 64, 0, InetAddress.getAllByName("localhost").map { it.toString() }))
	}

}