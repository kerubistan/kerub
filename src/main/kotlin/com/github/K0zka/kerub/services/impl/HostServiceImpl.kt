package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.host.SshClientService
import com.github.K0zka.kerub.host.getSshFingerPrint
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.HostPubKey
import com.github.K0zka.kerub.services.HostAndPassword
import com.github.K0zka.kerub.services.HostService

class HostServiceImpl(
		dao: HostDao,
		private val manager: HostManager,
		private val sshClientService: SshClientService)
: ListableBaseService<Host>(dao, "host"), HostService {

	override fun getPubkey(): String
			= sshClientService.getPublicKey()

	override fun joinWithoutPassword(host: Host): Host
			= manager.join(host)

	override fun join(hostPwd: HostAndPassword): Host
			= manager.join(hostPwd.host, hostPwd.password)

	override fun getHostPubkey(address: String): HostPubKey {
		val publicKey = manager.getHostPublicKey(address)
		return HostPubKey(publicKey.algorithm, publicKey.format, getSshFingerPrint(publicKey))
	}
}