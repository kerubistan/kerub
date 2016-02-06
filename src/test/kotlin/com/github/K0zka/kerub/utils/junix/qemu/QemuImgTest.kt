package com.github.K0zka.kerub.utils.junix.qemu

import com.github.K0zka.kerub.model.io.VirtualDiskFormat
import com.github.K0zka.kerub.utils.toSize
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.ClientSession
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.io.ByteArrayInputStream

@RunWith(MockitoJUnitRunner::class) class QemuImgTest {
	@Mock
	var session : ClientSession? = null

	@Mock
	var execChannel : ChannelExec? = null

	@Mock
	var channelOpenFuture : OpenFuture? = null

	@Before
	fun setup() {
		Mockito.`when`(session!!.createExecChannel(Matchers.anyString() ?: "")).thenReturn(execChannel!!)
		Mockito.`when`(execChannel!!.open()).thenReturn(channelOpenFuture)
	}

	@Test
	fun create() {
		Mockito.`when`(execChannel!!.invertedOut).thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel!!.invertedErr).thenReturn(NullInputStream(0))

		QemuImg.create(session!!, VirtualDiskFormat.raw, "100 MB".toSize(), "/tmp/test.raw")

		Mockito.verify(session!!).createExecChannel("qemu-img create -f raw /tmp/test.raw ${"100 MB".toSize()}")
	}

	@Test
	fun info() {
		Mockito.`when`(session!!.createExecChannel(Matchers.anyString() ?: "")).thenReturn(execChannel!!)
		Mockito.`when`(execChannel!!.open()).thenReturn(channelOpenFuture)
		val testOutput = """
{
    "virtual-size": 1073741824,
    "filename": "tmp/test.raw",
    "format": "raw",
    "actual-size": 0,
    "dirty-flag": false
}
"""
		Mockito.`when`(execChannel!!.invertedOut).thenReturn(ByteArrayInputStream(testOutput.toByteArray(charset("UTF-8"))))
		Mockito.`when`(execChannel!!.invertedErr).thenReturn(NullInputStream(0))

		val info = QemuImg.info(session!!, "/tmp/test.raw")

		Mockito.verify(session!!).createExecChannel("qemu-img info /tmp/test.raw")
		Assert.assertThat(info.virtualSize, CoreMatchers.equalTo(1073741824.toLong()))
		Assert.assertThat(info.diskSize, CoreMatchers.equalTo(0.toLong()))
		Assert.assertThat(info.fileName, CoreMatchers.`is`("tmp/test.raw"))
		Assert.assertThat(info.format, CoreMatchers.`is`(VirtualDiskFormat.raw))

	}

	@Test
	fun resize() {
		Mockito.`when`(execChannel!!.invertedOut).thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel!!.invertedErr).thenReturn(NullInputStream(0))

		QemuImg.resize(session!!, "/tmp/test.raw", "100 MB".toSize())

		Mockito.verify(session!!).createExecChannel("qemu-img resize /tmp/test.raw ${"100 MB".toSize()}")
	}

	@Test
	fun convert() {
		Mockito.`when`(execChannel!!.invertedOut).thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel!!.invertedErr).thenReturn(NullInputStream(0))

		QemuImg.convert(session!!, "/tmp/test.raw", "/tmp/test.qcow2", VirtualDiskFormat.qcow2)

		Mockito.verify(session!!).createExecChannel("qemu-img convert -O qcow2 /tmp/test.raw /tmp/test.qcow2")
	}

}