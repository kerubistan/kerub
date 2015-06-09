package com.github.K0zka.kerub.utils.junix.rpm

import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.utils.SoftwarePackage
import com.github.K0zka.kerub.utils.version.Version
import org.apache.sshd.ClientSession

/**
 * Tool to get the list of packages with an rpm command.
 * It can be reused in RPM-based distribution adapters.
 */
public object RpmListPackages {
	fun execute(session: ClientSession): List<SoftwarePackage> =
			session.execute("rpm -qa --queryformat \"%{NAME}\\t%{VERSION}\\n\"")
					.trim()
					.split("\n".toRegex()).toTypedArray()
					.map {
						SoftwarePackage(it.substringBefore("\t"), Version.fromVersionString(it.substringAfter("\t")))
					}
}