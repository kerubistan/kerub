package com.github.K0zka.kerub.services.impl

import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.GET

class VersionInfo (val version: String, val vendor: String, val title: String) {

}

Produces("application/xml", "application/json")
Consumes("application/xml", "application/json")
Path("/meta/version")
class VersionService {
	Path("/")
	GET
	fun getVersionInfo(): VersionInfo {
		val pack = javaClass<VersionService>().getPackage()
		return VersionInfo(pack?.getImplementationVersion() ?: "dev",
		                   pack?.getImplementationVendor() ?: "",
		                   pack?.getImplementationTitle() ?: "")
	}
}