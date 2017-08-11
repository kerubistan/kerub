package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.host.SshClientService
import com.github.K0zka.kerub.host.getSshFingerPrint
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.HostPubKey
import com.github.K0zka.kerub.model.paging.SearchResultPage
import com.github.K0zka.kerub.services.HostAndPassword
import com.github.K0zka.kerub.services.HostJoinDetails
import com.github.K0zka.kerub.services.HostService
import java.util.UUID

class HostServiceImpl(
		override val dao: HostDao,
		private val manager: HostManager,
		private val sshClientService: SshClientService)
	: ListableBaseService<Host>("host"), HostService {

	override fun remove(id: UUID) =
			dao.update(id) {
				it.copy(
						recycling = true
				)
			}

	override fun declareDead(id: UUID) =
			dao.update(id) {
				it.copy(
						dead = true
				)
			}

	override fun getByAddress(address: String): List<Host> = dao.byAddress(address)

	override fun search(field: String, value: String, start: Long, limit: Int): SearchResultPage<Host> =
			dao.fieldSearch(field, value, start, limit).let {
				SearchResultPage(
						start = start,
						count = it.size.toLong(),
						result = it,
						searchby = field,
						total = it.size.toLong()
				)
			}

	override fun getPubkey(): String
			= sshClientService.getPublicKey()

	override fun joinWithoutPassword(details: HostJoinDetails): Host
			= manager.join(details.host, details.powerManagement)

	override fun join(hostPwd: HostAndPassword): Host
			= manager.join(hostPwd.host, hostPwd.password)

	override fun getHostPubkey(address: String): HostPubKey {
		val publicKey = manager.getHostPublicKey(address)
		return HostPubKey(publicKey.algorithm, publicKey.format, getSshFingerPrint(publicKey))
	}
}