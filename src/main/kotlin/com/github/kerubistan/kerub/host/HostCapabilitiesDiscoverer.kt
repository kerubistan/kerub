package com.github.kerubistan.kerub.host

import com.github.kerubistan.kerub.host.distros.Distribution
import com.github.kerubistan.kerub.model.HostCapabilities
import org.apache.sshd.client.session.ClientSession

interface HostCapabilitiesDiscoverer {
	fun discoverHost(session: ClientSession, dedicated: Boolean = false): HostCapabilities
	fun detectDistro(session: ClientSession): Distribution?
}