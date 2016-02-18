package com.github.K0zka.kerub.planner.steps.vstorage.create

import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.eq
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.matchAny
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.on
import com.github.K0zka.kerub.utils.toSize
import org.apache.sshd.ClientSession
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
class CreateLvExecutorTest {

	@Mock
	var hostCommandExecutor : HostCommandExecutor? = null
	@Mock
	var virtualDiskDynDao: VirtualStorageDeviceDynamicDao? = null
	@Mock
	var session : ClientSession? = null
	@Mock
	var execChannel : ChannelExec? = null
	@Mock
	var openFuture : OpenFuture? = null


	val host = Host(
			id = UUID.randomUUID(),
			address = "host-1.example.com",
			dedicated = true,
			publicKey = ""
	)

	val vDisk = VirtualStorageDevice(
			id = UUID.randomUUID(),
			name = "system disk",
			size = "16 GB".toSize()
	)

	@Test
	fun execute() {

		//TODO: this is stub, not actually doing anything at all

		CreateLvExecutor(hostCommandExecutor!!, virtualDiskDynDao!!).execute(
				CreateLv(
						host = host,
						disk = vDisk,
						volumeGroupName = "testvg"
				)
		)
	}

}