package com.github.kerubistan.kerub.utils.junix.virt.vbox

import com.github.kerubistan.kerub.utils.junix.AbstractJunixCommandVerification
import com.github.kerubistan.kerub.utils.resource
import com.github.kerubistan.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.OutputStream

class VBoxManageTest : AbstractJunixCommandVerification() {

	@Test
	fun monitorVm() {
		val input = resource("com/github/kerubistan/kerub/utils/junix/virt/vbox/vboxmanage-metrics-collect.txt")
		val callback = mock<(ts: Long, metrics: VBoxMetrics) -> Unit>()
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		doAnswer {
			input.copyTo(it.arguments[0] as OutputStream)
		}.whenever(execChannel).out = any()

		VBoxManage.monitorVm(session, "TEST-VM", callback)

		verify(callback, times(6)).invoke(any(), any())
	}


	@Test
	fun round() {
		assertEquals("512", VBoxManage.round("511.11 MB".toSize()))
		assertEquals("512", VBoxManage.round("511.5 MB".toSize()))
		assertEquals("2", VBoxManage.round("1025 KB".toSize()))
		assertEquals("1", VBoxManage.round("1024 KB".toSize()))
		assertEquals("0", VBoxManage.round("0 KB".toSize()))
		assertEquals("1", VBoxManage.round("1 KB".toSize()))
	}

}