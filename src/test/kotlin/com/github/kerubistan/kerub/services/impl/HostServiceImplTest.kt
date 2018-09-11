package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.HostDao
import com.github.kerubistan.kerub.getTestKey
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.host.SshClientService
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.lom.PowerManagementInfo
import com.github.kerubistan.kerub.services.HostAndPassword
import com.github.kerubistan.kerub.services.HostJoinDetails
import com.github.kerubistan.kerub.testHost
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import java.security.PublicKey
import java.util.UUID
import kotlin.test.assertEquals

class HostServiceImplTest {
	private val dao: HostDao = mock()
	private val manager: HostManager = mock()
	private val sshClientService: SshClientService = mock()

	private var pubKey: PublicKey = getTestKey().public

	private var service: HostServiceImpl? = null

	@Before
	fun setup() {
		service = HostServiceImpl(dao, manager, sshClientService)
	}

	@Test
	fun join() {
		val hostAndPwd = HostAndPassword(
				password = "TEST",
				host = Host(
						id = UUID.randomUUID(),
						address = "127.0.0.1",
						publicKey = "TEST",
						dedicated = true,
						capabilities = null
				)
		)
		service!!.join(hostAndPwd)
		verify(manager).join(eq(hostAndPwd.host), eq(hostAndPwd.password), eq(listOf()))
	}

	@Test
	fun getByAddress() {
		whenever(dao.byAddress(eq("test.example.com") ?: "")).thenReturn(listOf(testHost))
		val byAddress = service!!.getByAddress("test.example.com")
		assertEquals(listOf(testHost), byAddress)
		verify(dao).byAddress(eq("test.example.com"))
	}

	@Test
	fun joinWithoutPassword() {
		val host = Host(
				id = UUID.randomUUID(),
				address = "127.0.0.1",
				publicKey = "TEST",
				dedicated = true,
				capabilities = null
		)
		service!!.joinWithoutPassword(HostJoinDetails(host = host) )
		verify(manager).join(eq(host), eq(listOf<PowerManagementInfo>()))
	}

	@Test
	fun getHostPubkey() {
		whenever(manager.getHostPublicKey(anyString())).thenReturn(pubKey)

		val hostPubKey = service!!.getHostPubkey("127.0.0l.1")
		Assert.assertThat(hostPubKey.algorithm, CoreMatchers.`is`(pubKey.algorithm))
		Assert.assertThat(hostPubKey.format, CoreMatchers.`is`(pubKey.format))
		Assert.assertThat(hostPubKey.fingerprint, CoreMatchers.`is`("f6:aa:fa:c7:1d:98:cd:8b:0c:5b:c6:63:bb:3a:73:f6"))
	}

	@Test
	fun getPubKey() {
		whenever(sshClientService.getPublicKey()).thenReturn("TEST-KEY")

		Assert.assertThat(service!!.getPubkey(), CoreMatchers.`is`("TEST-KEY"))

		verify(sshClientService).getPublicKey()
	}
}