package com.github.K0zka.kerub.utils.junix.packagemanager.emerge

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import org.apache.sshd.client.session.ClientSession

object Emerge {

	val releasePattern = "r\\d+".toRegex()

	fun listPackages(session: ClientSession): List<SoftwarePackage> =
			session.executeOrDie("ls -d /var/db/pkg/*/*| cut -f5- -d/").lines().map {
				if (it.substringAfterLast("-").matches(releasePattern)) {
					val name = it.substringBeforeLast("-").substringBeforeLast("-")
					SoftwarePackage(
							name = name,
							version = Version.fromVersionString(it.substringAfter(name).substringAfter("-"))
					)
				} else {
					SoftwarePackage(
							name = it.substringBeforeLast("-"),
							version = Version.fromVersionString(it.substringAfterLast("-"))
					)
				}
			}

}