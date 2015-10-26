package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.services.VersionInfo
import com.github.K0zka.kerub.services.VersionService
import com.github.K0zka.kerub.utils.getLogger


class VersionServiceImpl : VersionService {

	private val version = buildVersionInfo()

	override fun getVersionInfo(): VersionInfo {
		return version
	}

	companion object {
		private val logger = getLogger(VersionServiceImpl::class)
		fun buildVersionInfo(): VersionInfo {
			val pack = VersionServiceImpl::class.java.getPackage()
			return VersionInfo(pack?.getImplementationVersion() ?: "dev",
			                   pack?.getImplementationVendor() ?: "",
			                   pack?.getImplementationTitle() ?: "kerub")
		}
	}

	fun logStart() {
		logger.info("Starting {} version {} ", version.title, version.version)
	}

	fun logStop() {
		logger.info("Stopping {} version {} ", version.title, version.version)
	}

}