package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.model.HostCapabilities
import org.apache.sshd.ClientSession

public interface HostCapabilitiesDiscoverer {
	fun discoverHost(session: ClientSession, dedicated : Boolean = false): HostCapabilities
}