package com.github.K0zka.kerub.utils.junix.packagemanager.cygwin

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.skip
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