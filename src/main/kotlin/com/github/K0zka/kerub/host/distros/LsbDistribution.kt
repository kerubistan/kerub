package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.getFileContents
import org.apache.sshd.ClientSession
import com.github.K0zka.kerub.utils.version.Version
import java.util.Properties
import java.io.StringReader

public abstract class LsbDistribution(val distroName : String) : Distribution {

	override fun name(): String {
		return distroName
	}

	override fun getVersion(session: ClientSession): Version {
		val versionString = readLsbReleaseProperties(session)
				.getProperty("VERSION_ID")
				?.replaceAll("\"", "")
		return Version.fromVersionString(
					versionString ?: "unknown")
	}

	override fun detect(session: ClientSession): Boolean {
		return distroName.equalsIgnoreCase(
				readLsbReleaseProperties(session)
						.getProperty("NAME")
						?.replaceAll("\"", "") ?: "unknown")
	}


	protected fun readLsbReleaseProperties(session: ClientSession): Properties {
		val props = Properties()
		StringReader(session.getFileContents("/etc/os-release")).use {
			props.load(it)
		}
		return props
	}

}