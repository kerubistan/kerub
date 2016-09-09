package com.github.K0zka.kerub.utils.junix.df

import com.github.K0zka.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.mock
import junit.framework.TestCase
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mockito
import java.io.ByteArrayInputStream

class DFTest {

	val session : ClientSession = mock()
	val execChannel : ChannelExec = mock()
	val channelOpenFuture : OpenFuture = mock()

val testDfOutput = """Filesystem                                      1024-blocks     Used Available Capacity Mounted on
devtmpfs                                            3871128        0   3871128       0% /dev
tmpfs                                               3883208   102756   3780452       3% /dev/shm
tmpfs                                               3883208     1300   3881908       1% /run
tmpfs                                               3883208        0   3883208       0% /sys/fs/cgroup
/dev/mapper/fedora_dhcp130--218-root               51475068 39591128   9246116      82% /
tmpfs                                               3883208     7668   3875540       1% /tmp
/dev/sda1                                            487652   140727    317229      31% /boot
/dev/mapper/fedora_dhcp130--218-home              100660656 83351096  12173176      88% /home
/dev/mapper/fedora_dhcp130--218-var_lib_libvirt    81420076 68844646   9504285      88% /var/lib/libvirt
tmpfs                                                776644        4    776640       1% /run/user/993
tmpfs                                                776644       24    776620       1% /run/user/1000
"""

	@Before
	fun setup() {
		Mockito.`when`(session.createExecChannel(Matchers.anyString() ?: "")).thenReturn(execChannel)
		Mockito.`when`(execChannel.open()).thenReturn(channelOpenFuture)
	}

	@Test
	fun df() {
		Mockito.`when`(execChannel.invertedOut).thenReturn(ByteArrayInputStream(testDfOutput.toByteArray(charset("ASCII"))))
		Mockito.`when`(execChannel.invertedErr).thenReturn(NullInputStream(0))

		val mounts = DF.df(session)

		Assert.assertEquals(11, mounts.size)
		val libvirt = mounts.first { it.mountPoint == "/var/lib/libvirt" }
		Assert.assertEquals("9732387840 B".toSize(), libvirt.free)
		Assert.assertEquals("70496917504 B".toSize(), libvirt.used)
	}
}