package com.github.kerubistan.kerub.utils.junix.packagemanager.cygwin

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import io.github.kerubistan.kroki.collections.skip
import org.apache.sshd.client.session.ClientSession

object CygCheck {
	private val spaces = "\\s+".toRegex()
	fun listPackages(session: ClientSession): List<SoftwarePackage> {
		return session.executeOrDie("cygcheck -d -c").lines().skip().skip().filter { it.isNotEmpty() }.map {
			line ->
			val columns = line.split(spaces)
			SoftwarePackage(name = columns[0], version = Version.fromVersionString(columns[1].trim()))
		}
	}
}