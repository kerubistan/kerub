package com.github.kerubistan.kerub.utils.junix.lshw

import com.fasterxml.jackson.databind.DeserializationFeature
import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.utils.createObjectMapper
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession

object Lshw : OsCommand {

	//unfortunately lshw is for linux only
	override fun available(hostCapabilities: HostCapabilities?): Boolean
			= hostCapabilities?.os == OperatingSystem.Linux
			&& hostCapabilities.installedSoftware.any { it.name == "lshw" }

	private val mapper
			= createObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

	fun list(session: ClientSession): System
			= mapper.readValue(session.executeOrDie("lshw -json"), System::class.java)

}