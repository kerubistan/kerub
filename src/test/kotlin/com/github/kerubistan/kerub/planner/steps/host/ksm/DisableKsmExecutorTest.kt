package com.github.kerubistan.kerub.planner.steps.host.ksm

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import org.mockito.Mockito
import java.util.UUID
import kotlin.test.assertFalse

class DisableKsmExecutorTest {

	private val exec: HostCommandExecutor = mock()
	private val hostDynDao: HostDynamicDao = mock()

	val host = Host(
			id = UUID.randomUUID(),
			address = "host-1.example.com",
			dedicated = true,
			publicKey = ""
	)

	@Test
	fun execute() {
		var updatedHostDyn: HostDynamic? = null
		val hostDynamic = HostDynamic(id = host.id, status = HostStatus.Up)
		whenever(hostDynDao.update(any(), any(), any())).then {
			updatedHostDyn = (it.arguments[2] as (HostDynamic) -> HostDynamic).invoke(hostDynamic)
			updatedHostDyn
		}

		DisableKsmExecutor(exec, hostDynDao).execute(DisableKsm(
				host = host
		))

		Mockito.verify(exec).execute(Mockito.eq(host) ?: host, any<(ClientSession) -> Unit>())
		Mockito.verify(hostDynDao).update(
				eq(host.id),
				any(),
				any()
		)

		assertFalse("KSM should be idsabled") { (hostDynamic.ksmEnabled) }
	}
}