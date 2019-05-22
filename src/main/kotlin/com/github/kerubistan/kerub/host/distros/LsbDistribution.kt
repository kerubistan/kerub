package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.host.checkFileExists
import com.github.kerubistan.kerub.host.getFileContents
import com.github.kerubistan.kerub.model.Version
import org.apache.sshd.client.session.ClientSession
import java.io.StringReader
import java.util.Properties

abstract class LsbDistribution(private val distroName: String) : AbstractLinux() {

	override fun name(): String {
		return distroName
	}

	private fun <T> enforce(value: T?, msg: String): T {
		if (value == null) {
			throw IllegalArgumentException("$msg - value is null")
		} else {
			return value
		}
	}

	override fun getVersion(session: ClientSession): Version {
		return Version.fromVersionString(
				enforce(readLsbReleaseProperties(session)
						.getProperty("VERSION_ID")
						?.replace("\"".toRegex(), ""), "VERSION_ID is not found in the properties file"))
	}

	override fun detect(session: ClientSession): Boolean {
		return session.checkFileExists("/etc/os-release")
				&& distroName.equals(
				enforce(readLsbReleaseProperties(session)
						.getProperty("NAME")
						?.replace("\"".toRegex(), ""), "NAME is not found in the properties file"), ignoreCase = true)
	}

	protected fun readLsbReleaseProperties(session: ClientSession): Properties {
		val props = Properties()
		val propFileContents = session.getFileContents("/etc/os-release")
		StringReader(propFileContents).use {
			props.load(it)
		}
		return props
	}

}
