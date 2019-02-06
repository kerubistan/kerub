package com.github.kerubistan.kerub.utils.junix.virt.vbox

import com.github.kerubistan.kerub.KB
import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.model.io.DeviceType
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.verifyCommandExecution
import com.github.kerubistan.kerub.testVm
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
import org.junit.jupiter.api.assertThrows
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
	fun stopVm() {
		session.mockCommandExecution("VBoxManage controlvm .* poweroff.*".toRegex())

		VBoxManage.stopVm(session, testVm)

		session.verifyCommandExecution("VBoxManage controlvm .* poweroff.*".toRegex())
	}

	@Test
	fun pauseVm() {
		session.mockCommandExecution("VBoxManage controlvm .* pause.*".toRegex())

		VBoxManage.pauseVm(session, testVm)

		session.verifyCommandExecution("VBoxManage controlvm .* pause.*".toRegex())
	}

	@Test
	fun resumeVm() {
		session.mockCommandExecution("VBoxManage controlvm .* resume.*".toRegex())

		VBoxManage.resumeVm(session, testVm)

		session.verifyCommandExecution("VBoxManage controlvm .* resume.*".toRegex())
	}

	@Test
	fun resetVm() {
		session.mockCommandExecution("VBoxManage controlvm .* reset.*".toRegex())

		VBoxManage.resetVm(session, testVm)

		session.verifyCommandExecution("VBoxManage controlvm .* reset.*".toRegex())
	}

	@Test
	fun createMedium() {
		session.mockCommandExecution("VBoxManage createmedium .*".toRegex())

		VBoxManage.createMedium(session, size = 1.TB, path = "/kerub/test",type = DeviceType.disk, format = VirtualDiskFormat.vdi)

		session.verifyCommandExecution("VBoxManage createmedium .*".toRegex())
	}

	@Test
	fun createMediumValidations() {
		assertThrows<IllegalStateException> {
			VBoxManage.createMedium(session, size = 1.TB, path = "/kerub/test",type = DeviceType.disk, format = VirtualDiskFormat.qcow2)
		}
		assertThrows<IllegalStateException> {
			VBoxManage.createMedium(session, size = (-1).TB, path = "/kerub/test",type = DeviceType.disk, format = VirtualDiskFormat.vdi)
		}
	}

		@Test
	fun round() {
		assertEquals("512", VBoxManage.round("511.11 MB".toSize()))
		assertEquals("512", VBoxManage.round("511.5 MB".toSize()))
		assertEquals("2", VBoxManage.round(1025.KB))
		assertEquals("1", VBoxManage.round(1024.KB))
		assertEquals("0", VBoxManage.round(0.KB))
		assertEquals("1", VBoxManage.round(1.KB))
	}

}