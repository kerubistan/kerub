package com.github.kerubistan.kerub.utils.junix.storagemanager.lvm

import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.mockProcess
import com.github.kerubistan.kerub.sshtestutils.verifyCommandExecution
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.math.BigInteger
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LvmVgTest {

	private val session: ClientSession = mock()

	private val testOutput = """  WfbuiJ-KniK-WBF9-h2ae-IwgM-k1Jh-671l51:fedora:9139388416B:4194304B:2179:1
  uPPT5K-Rtym-cxQX-f3iu-oiZf-M4Z3-t8v4We:test:4286578688B:1065353216B:1022:254
"""

	@Test
	fun list() {

		session.mockCommandExecution("lvm vgs.*", output = testOutput)

		val list = LvmVg.list(session)

		assertEquals(2, list.size)
		assertEquals("WfbuiJ-KniK-WBF9-h2ae-IwgM-k1Jh-671l51", list[0].id)
		assertEquals("fedora", list[0].name)
		assertEquals(BigInteger("4194304"), list[0].freeSize)
		assertEquals(BigInteger("9139388416"), list[0].size)
		assertEquals(1.toLong(), list[0].freePes)
		assertEquals(2179.toLong(), list[0].pes)
	}

	@Test
	fun listEmpty() {
		session.mockCommandExecution("lvm vgs.*", output = "\n")

		val list = LvmVg.list(session)

		assertTrue(list.isEmpty())
	}


	private val monitorOutput = """  RsDNYC-Un0h-QvhF-hMqe-dEny-Bjlg-qbeeZa:fedora_localshot:433800085504B:4194304B:103426:1
  2atkNG-TWI4-bg0a-7E2R-lVdA-nKps-IOxTlf:testarea:565660614656B:536644419584B:134864:127946
--end
  RsDNYC-Un0h-QvhF-hMqe-dEny-Bjlg-qbeeZa:fedora_localshot:433800085504B:4194304B:103426:1
  2atkNG-TWI4-bg0a-7E2R-lVdA-nKps-IOxTlf:testarea:565660614656B:536644419584B:134864:127946
--end
"""


	@Test
	fun monitor() {
		val execChannel: ChannelExec = mock()
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		session.mockProcess(".*lvm vgs.*".toRegex(),monitorOutput)

		val results = mutableListOf<List<VolumeGroup>>()
		LvmVg.monitor(session, {
			vgs ->
			results.add(vgs)
		})

		assertEquals(2, results.size)
		assert( results.all { it.size == 2 } )
	}

	@Test
	fun reduce() {
		session.mockCommandExecution("lvm vgreduce.*".toRegex())
		LvmVg.reduce(session, "test-vg", "/dev/sdf")
		session.verifyCommandExecution("lvm vgreduce.*".toRegex())
	}

}