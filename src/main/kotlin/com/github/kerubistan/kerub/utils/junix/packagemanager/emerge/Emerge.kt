package com.github.kerubistan.kerub.utils.junix.packagemanager.emerge

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import org.apache.sshd.client.session.ClientSession

object Emerge {

	private val releasePattern = "r\\d+".toRegex()

	fun listPackages(session: ClientSession): List<SoftwarePackage> =
			session.executeOrDie("ls -d /var/db/pkg/*/*| cut -f5- -d/").lines().map {
				if (it.substringAfterLast("-").matches(releasePattern)) {
					val name = it.substringBeforeLast("-").substringBeforeLast("-")
					SoftwarePackage(
							name = name.substringAfter("/"),
							version = Version.fromVersionString(it.substringAfter(name).substringAfter("-"))
					)
				} else {
					SoftwarePackage(
							name = it.substringAfter("/").substringBeforeLast("-"),
							version = Version.fromVersionString(it.substringAfterLast("-"))
					)
				}
			}

}