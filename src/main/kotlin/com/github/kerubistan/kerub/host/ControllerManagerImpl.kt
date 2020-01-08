package com.github.kerubistan.kerub.host

import com.github.kerubistan.kerub.data.dynamic.ControllerDynamicDao
import com.github.kerubistan.kerub.model.dynamic.ControllerDynamic
import com.github.kerubistan.kerub.utils.getLogger
import org.infinispan.manager.EmbeddedCacheManager
import java.io.IOException
import java.net.InetAddress

class ControllerManagerImpl(val dao: ControllerDynamicDao,
							val cacheManager: EmbeddedCacheManager)
	: ControllerManager {

	var id: String? = null

	private fun getHostName(): String? {
		return try {
			InetAddress.getLocalHost().hostName
		} catch (ioe: IOException) {
			null
		}
	}

	private fun initId() {
		if (id == null) {
			id = getHostName()
		}
	}

	override fun getControllerId(): String = id ?: cacheManager.address.toString()

	companion object {
		private val logger = getLogger(ControllerManagerImpl::class)
	}

	fun start() {
		initId()
		val address = getControllerId()
		logger.info("Cache address: {}", address)
		dao.add(ControllerDynamic(address, 64, 0, InetAddress.getAllByName("localhost").map { it.toString() }))
	}

}