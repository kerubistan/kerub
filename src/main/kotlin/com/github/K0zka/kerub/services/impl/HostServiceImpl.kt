package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.host.ControllerAssigner
import com.github.K0zka.kerub.host.HostCapabilitiesDiscoverer
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.host.SshClientService
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.HostPubKey
import com.github.K0zka.kerub.services.HostAndPassword
import com.github.K0zka.kerub.services.HostService
import org.apache.sshd.common.util.KeyUtils

public class HostServiceImpl(
		dao: HostDao,
		val manager: HostManager,
		val hostAssigner: ControllerAssigner,
		val sshClientService: SshClientService,
		val discoverer: HostCapabilitiesDiscoverer)
: ListableBaseService<Host>(dao, "host"), HostService {
	override fun join(hostPwd: HostAndPassword): Host {
		val session = sshClientService.loginWithPassword(
				address = hostPwd.host.address,
				userName = "root",
				password = hostPwd.password)
		sshClientService.installPublicKey(session)
		val capabilities = discoverer.discoverHost(session)

		val host = hostPwd.host.copy(capabilities = capabilities)

		dao.add(host)
		hostAssigner.assignController(host)

		return host
	}

	override fun getHostPubkey(address: String): HostPubKey {
		val publicKey = manager.getHostPublicKey(address)
		return HostPubKey(publicKey.getAlgorithm(), publicKey.getFormat(), KeyUtils.getFingerPrint(publicKey))
	}
}