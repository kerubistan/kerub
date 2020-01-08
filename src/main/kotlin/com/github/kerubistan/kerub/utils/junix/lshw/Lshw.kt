package com.github.kerubistan.kerub.utils.junix.lshw

import com.fasterxml.jackson.databind.DeserializationFeature
import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.utils.createObjectMapper
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession

object Lshw : OsCommand {

	override fun available(hostCapabilities: HostCapabilities?): Boolean
	//unfortunately lshw is for linux only
			= hostCapabilities?.os == OperatingSystem.Linux
			// but then it is always called lshw - or at least in the known linux distros
			&& "lshw" in hostCapabilities.index.installedPackageNames

	private val mapper = createObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

	fun list(session: ClientSession): System = mapper.readValue(session.executeOrDie("lshw -json"), System::class.java)

}