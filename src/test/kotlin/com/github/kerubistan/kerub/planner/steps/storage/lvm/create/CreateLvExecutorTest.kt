package com.github.kerubistan.kerub.planner.steps.storage.lvm.create

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.mockHost
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecutionSequence
import com.github.kerubistan.kerub.testLvmCapability
import com.nhaarman.mockito_kotlin.mock
import io.github.kerubistan.kroki.size.GB
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.util.UUID

class CreateLvExecutorTest {

	private val hostCommandExecutor: HostCommandExecutor = mock()
	private val virtualDiskDynDao: VirtualStorageDeviceDynamicDao = mock()

	private val host = Host(
			id = UUID.randomUUID(),
			address = "host-1.example.com",
			dedicated = true,
			publicKey = ""
	)

	private val vDisk = VirtualStorageDevice(
			id = UUID.randomUUID(),
			name = "system disk",
			size = 16.GB
	)

	@Test
	fun execute() {

		val session = mock<ClientSession>()
		hostCommandExecutor.mockHost(host, session)

		session.mockCommandExecutionSequence(
				"lvm lvs.*".toRegex(),
				listOf(
						"""  fedora:Mvd5u6-pTbR-SUS2-sd2l-kx41-a0bx-YGuWcK:root:/dev/fedora/root:9135194112B:::linear:
  test:U3xf04-DJFM-nXD5-682c-9w32-rcjS-pne6t2:testlv2:/dev/test/testlv2:1073741824B:::linear:""",
						"""  fedora:Mvd5u6-pTbR-SUS2-sd2l-kx41-a0bx-YGuWcK:root:/dev/fedora/root:9135194112B:::linear:
  test:eCuTKA-rIDz-dzJq-48pK-DtqJ-X77p-YStcLS:${vDisk.id}:/dev/test/${vDisk.id}:1073741824B:::linear:
  test:U3xf04-DJFM-nXD5-682c-9w32-rcjS-pne6t2:testlv2:/dev/test/testlv2:1073741824B:::linear:""",
						"  test:eCuTKA-rIDz-dzJq-48pK-DtqJ-X77p-YStcLS:${vDisk.id}:/dev/test/${vDisk.id}:1073741824B:::linear:"
				)
		)

		session.mockCommandExecution("lvm lvcreate .*")
		CreateLvExecutor(hostCommandExecutor, virtualDiskDynDao).execute(
				CreateLv(
						host = host,
						disk = vDisk,
						capability = testLvmCapability
				)
		)
	}

}