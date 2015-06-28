package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.checkFileExists
import com.github.K0zka.kerub.host.getFileContents
import com.github.K0zka.kerub.model.Version
import org.apache.sshd.ClientSession
import java.io.StringReader
import java.util.Properties

public abstract class LsbDistribution(val distroName : String) : AbstractLinux() {

	override fun name(): String {
		return distroName
	}

	fun enforce<T>( value : T?, msg : String ) : T {
		if(value == null) {
			throw IllegalArgumentException("${msg} - value is null")
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
