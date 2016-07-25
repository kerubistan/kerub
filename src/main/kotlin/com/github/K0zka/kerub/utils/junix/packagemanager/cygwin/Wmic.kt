package com.github.K0zka.kerub.utils.junix.packagemanager.cygwin

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.skip
import org.apache.sshd.client.session.ClientSession
import java.nio.charset.Charset

object Wmic {

	private val spaces = "\\s+".toRegex()

	fun list(session : ClientSession) : List<SoftwarePackage> {
		val output = session.executeOrDie("wmic product list", { false }, charset("UTF-16")).lines()
		val header = output.first()
		val data = output.skip().filter { it.isNotEmpty() }

		val nameStart = header.indexOf("Name")
		val nameEnd = header.indexOf("PackageCache")

		val versionStart = header.indexOf("Version")

		return data.map {
			pack ->
			SoftwarePackage(name = pack.substring(nameStart, nameEnd).trim(), version = Version.fromVersionString(pack.substring(versionStart).trim()))
		}
	}
}