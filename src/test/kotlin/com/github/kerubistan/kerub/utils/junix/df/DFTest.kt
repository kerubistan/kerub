package com.github.kerubistan.kerub.utils.junix.df

import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.mockProcess
import com.github.kerubistan.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert
import org.junit.Test

class DFTest {

	val session: ClientSession = mock()

	private val testDfOutput =
			"""Filesystem                                      1024-blocks     Used Available Capacity Mounted on
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

	@Test
	fun df() {
		session.mockCommandExecution("df.*".toRegex(), output = testDfOutput)

		val mounts = DF.df(session)

		Assert.assertEquals(11, mounts.size)
		val libvirt = mounts.first { it.mountPoint == "/var/lib/libvirt" }
		Assert.assertEquals("9732387840 B".toSize(), libvirt.free)
		Assert.assertEquals("70496917504 B".toSize(), libvirt.used)
	}

	private val monitorOutput = """Filesystem                        1024-blocks      Used Available Capacity Mounted on
udev                                  8150996         0   8150996       0% /dev
tmpfs                                 1634472      9868   1624604       1% /run
/dev/mapper/fedora_localshot-root    51475068   8868540  39968704      19% /
tmpfs                                 8172360      1048   8171312       1% /dev/shm
tmpfs                                    5120         4      5116       1% /run/lock
tmpfs                                 8172360         0   8172360       0% /sys/fs/cgroup
/dev/sda2                              487652    244317    213639      54% /boot
/dev/sda1                              204580     13144    191436       7% /boot/efi
/dev/mapper/fedora_localshot-home   154693604 142239536   4572956      97% /home
/dev/mapper/fedora_localshot-var    206293688 180935332  14856212      93% /var
cgmfs                                     100         0       100       0% /run/cgmanager/fs
tmpfs                                 1634472        24   1634448       1% /run/user/1000
--separator
Filesystem                        1024-blocks      Used Available Capacity Mounted on
udev                                  8150996         0   8150996       0% /dev
tmpfs                                 1634472      9868   1624604       1% /run
/dev/mapper/fedora_localshot-root    51475068   8868540  39968704      19% /
tmpfs                                 8172360      1048   8171312       1% /dev/shm
tmpfs                                    5120         4      5116       1% /run/lock
tmpfs                                 8172360         0   8172360       0% /sys/fs/cgroup
/dev/sda2                              487652    244317    213639      54% /boot
/dev/sda1                              204580     13144    191436       7% /boot/efi
/dev/mapper/fedora_localshot-home   154693604 142239568   4572924      97% /home
/dev/mapper/fedora_localshot-var    206293688 180935332  14856212      93% /var
cgmfs                                     100         0       100       0% /run/cgmanager/fs
tmpfs                                 1634472        24   1634448       1% /run/user/1000
--separator
Filesystem                        1024-blocks      Used Available Capacity Mounted on
udev                                  8150996         0   8150996       0% /dev
tmpfs                                 1634472      9868   1624604       1% /run
/dev/mapper/fedora_localshot-root    51475068   8868540  39968704      19% /
tmpfs                                 8172360      1048   8171312       1% /dev/shm
tmpfs                                    5120         4      5116       1% /run/lock
tmpfs                                 8172360         0   8172360       0% /sys/fs/cgroup
/dev/sda2                              487652    244317    213639      54% /boot
/dev/sda1                              204580     13144    191436       7% /boot/efi
/dev/mapper/fedora_localshot-home   154693604 142239572   4572920      97% /home
/dev/mapper/fedora_localshot-var    206293688 180935332  14856212      93% /var
cgmfs                                     100         0       100       0% /run/cgmanager/fs
tmpfs                                 1634472        24   1634448       1% /run/user/1000
--separator"""

	@Test
	fun monitor() {
		session.mockProcess(".*".toRegex(), output = monitorOutput)
		val callback = mock<(List<FilesystemInfo>) -> Unit>()

		DF.monitor(session, callback = callback)

		verify(callback, times(3)).invoke(any())
	}
}