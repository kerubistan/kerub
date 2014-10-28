package com.github.K0zka.kerub.services.impl

import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.GET
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiResponses
import com.wordnik.swagger.annotations.ApiResponse
import com.wordnik.swagger.annotations.ApiModel
import com.wordnik.swagger.annotations.ApiModelProperty
import com.github.K0zka.kerub.services.VersionInfo
import com.github.K0zka.kerub.services.VersionService
import com.github.K0zka.kerub.utils.getLogger


class VersionServiceImpl : VersionService {

	private val version = buildVersionInfo()

	override fun getVersionInfo(): VersionInfo {
		return version
	}

	class object {
		private val logger = getLogger(javaClass<VersionServiceImpl>())
		fun buildVersionInfo(): VersionInfo {
			val pack = javaClass<VersionServiceImpl>().getPackage()
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