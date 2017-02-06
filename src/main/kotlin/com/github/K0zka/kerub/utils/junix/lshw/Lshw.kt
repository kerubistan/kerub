package com.github.K0zka.kerub.utils.junix.lshw

import com.fasterxml.jackson.databind.DeserializationFeature
import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.model.HostCapabilities
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.utils.createObjectMapper
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession

object Lshw : OsCommand {

	//unfortunately lshw is for linux only
	override fun available(hostCapabilities: HostCapabilities?): Boolean
			= hostCapabilities?.os == OperatingSystem.Linux
			&& hostCapabilities?.installedSoftware?.any { it.name == "lshw" } ?: false

	private val mapper
			= createObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)

	fun list(session: ClientSession): System
			= mapper.readValue(session.executeOrDie("lshw -json"), System::class.java)

}