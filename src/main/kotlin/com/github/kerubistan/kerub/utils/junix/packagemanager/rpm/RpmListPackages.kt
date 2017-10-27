package com.github.kerubistan.kerub.utils.junix.packagemanager.rpm

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import org.apache.sshd.client.session.ClientSession

/**
 * Tool to get the list of packages with an rpm command.
 * It can be reused in RPM-based distribution adapters.
 */
object RpmListPackages {
	fun execute(session: ClientSession): List<SoftwarePackage> =
			session.executeOrDie("rpm -qa --queryformat \"%{NAME}\\t%{VERSION}\\n\"")
					.trim()
					.split("\n".toRegex()).toTypedArray()
					.map {
						SoftwarePackage(it.substringBefore("\t"), Version.fromVersionString(it.substringAfter("\t")))
					}
}