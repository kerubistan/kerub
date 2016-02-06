package com.github.K0zka.kerub.utils.junix.storagemanager.lvm

import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.ClientSession
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.io.ByteArrayInputStream
import java.math.BigInteger

@RunWith(MockitoJUnitRunner::class)
class LvmVgTest {

	@Mock
	var session: ClientSession? = null

	@Mock
	var execChannel: ChannelExec? = null

	@Mock
	var openFuture : OpenFuture? = null

	val testOutput = """  WfbuiJ-KniK-WBF9-h2ae-IwgM-k1Jh-671l51,fedora,9139388416B,4194304B,2179,1
  uPPT5K-Rtym-cxQX-f3iu-oiZf-M4Z3-t8v4We,test,4286578688B,1065353216B,1022,254
"""

	@Test
	fun list() {

		Mockito.`when`(session?.createExecChannel(Matchers.startsWith("vgs"))).thenReturn(execChannel)
		Mockito.`when`(execChannel?.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel?.invertedOut).thenReturn(ByteArrayInputStream(testOutput.toByteArray(charset("ASCII"))))
		Mockito.`when`(execChannel?.invertedErr).thenReturn(NullInputStream(0))


		val list = LvmVg.list(session!!)

		Assert.assertEquals(2, list.size)
		Assert.assertEquals("WfbuiJ-KniK-WBF9-h2ae-IwgM-k1Jh-671l51", list[0].id)
		Assert.assertEquals("fedora", list[0].name)
		Assert.assertEquals(BigInteger("4194304"), list[0].freeSize)
		Assert.assertEquals(BigInteger("9139388416"), list[0].size)
		Assert.assertEquals(1.toLong(), list[0].freePes)
		Assert.assertEquals(2179.toLong(), list[0].pes)
	}

}