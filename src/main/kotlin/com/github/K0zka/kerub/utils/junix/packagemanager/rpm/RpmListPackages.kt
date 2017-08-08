package com.github.K0zka.kerub.utils.junix.packagemanager.rpm

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
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