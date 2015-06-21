package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.getTestKey
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.services.HostAndPassword
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers.anyString
import org.mockito.Matchers.eq
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.security.PublicKey
import java.util.UUID

RunWith(MockitoJUnitRunner::class)
public class HostServiceImplTest {
	Mock
	var dao: HostDao? = null
	Mock
	var manager: HostManager? = null

	var pubKey: PublicKey = getTestKey().getPublic()

	var service: HostServiceImpl? = null

	Before
	fun setup() {
		service = HostServiceImpl(dao!!, manager!!)
	}

	Test
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
		Mockito.verify(manager)!!.join(eq(hostAndPwd.host) ?: hostAndPwd.host, eq(hostAndPwd.password) ?: hostAndPwd.password)
	}

	Test
	fun getHostPubkey() {
		Mockito.`when`(manager!!.getHostPublicKey(anyString())).thenReturn(pubKey)

		val hostPubKey = service!!.getHostPubkey("127.0.0l.1")
		Assert.assertThat(hostPubKey.algorithm, CoreMatchers.`is`(pubKey!!.getAlgorithm()))
		Assert.assertThat(hostPubKey.format, CoreMatchers.`is`(pubKey!!.getFormat()))


	}
}