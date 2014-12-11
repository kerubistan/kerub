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

	inline fun enforce<T>( value : T?, msg : String ) : T {
		if(value == null) {
			throw IllegalArgumentException("${msg} - value is null")
		} else {
			return value
		}
	}

	override fun getVersion(session: ClientSession): Version {
		val versionString = readLsbReleaseProperties(session)
				.getProperty("VERSION_ID")
				?.replaceAll("\"", "")
		return Version.fromVersionString(
				enforce(readLsbReleaseProperties(session)
						.getProperty("VERSION_ID")
						?.replaceAll("\"", ""), "VERSION_ID is not found in the properties file"))
	}

	override fun detect(session: ClientSession): Boolean {
		return distroName.equalsIgnoreCase(
				enforce(readLsbReleaseProperties(session)
						.getProperty("NAME")
						?.replaceAll("\"", ""), "NAME is not found in the properties file"))
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
