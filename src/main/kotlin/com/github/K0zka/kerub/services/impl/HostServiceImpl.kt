package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.services.HostService
import java.util.UUID
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.ListableDao
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.services.HostPubKey

public class HostServiceImpl(dao: HostDao, val manager: HostManager) : ListableBaseService<Host>(dao, "host"), HostService {
	override fun getHostPubkey(address: String): HostPubKey {
		val publicKey = manager.getHostPublicKey(address)
		return HostPubKey(publicKey.getAlgorithm(), publicKey.getFormat(), publicKey.getEncoded()!!)
	}
}