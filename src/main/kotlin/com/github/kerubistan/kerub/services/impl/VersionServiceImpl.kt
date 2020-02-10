package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.services.VersionInfo
import com.github.kerubistan.kerub.services.VersionService
import com.github.kerubistan.kerub.utils.getLogger


class VersionServiceImpl : VersionService {

	private val version = buildVersionInfo()

	override fun getVersionInfo(): VersionInfo {
		return version
	}

	companion object {
		private val logger = getLogger()
		fun buildVersionInfo(): VersionInfo {
			val pack = VersionServiceImpl::class.java.`package`
			return VersionInfo(pack?.implementationVersion ?: "dev",
					pack?.implementationVendor ?: "",
					pack?.implementationTitle ?: "kerub")
		}
	}

	fun logStart() {
		logger.info("Starting {} version {} ", version.title, version.version)
	}

	fun logStop() {
		logger.info("Stopping {} version {} ", version.title, version.version)
	}

}