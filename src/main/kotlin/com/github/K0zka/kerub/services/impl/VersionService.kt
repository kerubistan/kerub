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

ApiModel("VersionInfo", description = "Version information")
class VersionInfo (
		ApiModelProperty(required = true, value = "Version") val version: String,
		ApiModelProperty(required = true, value = "Vendor") val vendor: String,
		ApiModelProperty(required = true, value = "Title") val title: String) {

}

Api(description = "Version information", value = "s/r/meta/version")
Produces("application/json")
Consumes("application/json")
Path("/meta/version")
class VersionService {

	private val versionInfo = buildVersionInfo()

	ApiOperation(value = "Get version", notes = "Get the Kerub version")
	ApiResponses(ApiResponse(code = 200, message = "OK", response = javaClass<VersionInfo>()))
	Path("/")
	GET
	fun getVersionInfo(): VersionInfo {
		return versionInfo
	}

	class object fun buildVersionInfo(): VersionInfo {
		val pack = javaClass<VersionService>().getPackage()
		return VersionInfo(pack?.getImplementationVersion() ?: "dev",
		                   pack?.getImplementationVendor() ?: "",
		                   pack?.getImplementationTitle() ?: "")
	}
}