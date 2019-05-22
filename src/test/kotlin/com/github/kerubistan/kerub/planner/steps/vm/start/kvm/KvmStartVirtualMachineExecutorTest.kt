package com.github.kerubistan.kerub.planner.steps.vm.start.kvm

import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.host.FireWall
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.Range
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.size.MB
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Test
import org.mockito.Mockito
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.util.UUID
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathFactory
import kotlin.test.assertEquals

class KvmStartVirtualMachineExecutorTest {

	val host = Host(
			address = "127.0.0.1",
			publicKey = "",
			dedicated = false
	)

	val vm = VirtualMachine(
			name = "",
			id = UUID.randomUUID(),
			nrOfCpus = 1,
			memory = Range(128.MB, 128.MB)
	)

	val step = KvmStartVirtualMachine(
			host = host,
			vm = vm
	)

	val hostManager = mock<HostManager>()
	private val vmDynDao = mock<VirtualMachineDynamicDao>()
	private val firewall = mock<FireWall>()
	val hostCommandExecutor = mock<HostCommandExecutor>()
	val session = mock<ClientSession>()
	val sftp = mock<SftpClient>()

	@Test
	fun executeWithFailingGetDisplay() {
		session.mockCommandExecution(
				commandMatcher = "virsh domdisplay.*",
				output = "\n",
				// familiar message from opensuse 42
				error = "One or more references were leaked after disconnect from the hypervisor"
		)

		session.mockCommandExecution(
				"virsh create.*"
		)

		whenever(session.createSftpClient()).thenReturn(sftp)
		val domainXml = ByteArrayOutputStream()
		whenever(sftp.write(argThat { startsWith("/tmp") && endsWith(".xml") })).thenReturn(domainXml)
		whenever(hostManager.getFireWall(Mockito.any(Host::class.java) ?: host)).thenReturn(firewall)

		KvmStartVirtualMachineExecutor(hostManager, vmDynDao, hostCommandExecutor).execute(step)

		// because spice port could not be read
		verify(firewall, never()).open(any(), any())
		// and also the display port won't be set
		verify(vmDynDao).update(argThat { displaySetting == null })

	}

		@Test
	fun execute() {

		session.mockCommandExecution(
				commandMatcher = "virsh domdisplay.*",
				output = "spice://localhost:5902\n"
		)

		session.mockCommandExecution(
				"virsh create.*"
		)

		whenever(session.createSftpClient()).thenReturn(sftp)
		val domainXml = ByteArrayOutputStream()
		whenever(sftp.write(argThat { startsWith("/tmp") && endsWith(".xml") })).thenReturn(domainXml)
		whenever(hostManager.getFireWall(Mockito.any(Host::class.java) ?: host)).thenReturn(firewall)
		doAnswer {
			(it.arguments[1] as (ClientSession) -> Any).invoke(session)
		}.whenever(hostCommandExecutor).execute(eq(host), any<(ClientSession) -> Any>())

		KvmStartVirtualMachineExecutor(hostManager, vmDynDao, hostCommandExecutor).execute(step)

		verify(firewall).open(eq(5902), eq("tcp"))
		verify(vmDynDao).update(Mockito.any(VirtualMachineDynamic::class.java) ?: VirtualMachineDynamic(
				id = step.vm.id,
				status = VirtualMachineStatus.Up,
				memoryUsed = BigInteger.ZERO,
				hostId = step.host.id
		))
		checkDomainXml(domainXml, vm)
	}

	private fun checkDomainXml(domainXml: ByteArrayOutputStream, vm: VirtualMachine) {
		// verify that the domain is a correct XML
		val document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(ByteArrayInputStream(domainXml.toByteArray()))
		val xPath = XPathFactory.newInstance().newXPath()
		assertEquals(vm.id.toString(), xPath.evaluate("/domain/name/text()", document))
		assertEquals(vm.id.toString(), xPath.evaluate("/domain/uuid/text()", document))
		assertEquals("kvm", xPath.evaluate("/domain/@type", document))
		assertEquals(vm.nrOfCpus.toString(), xPath.evaluate("/domain/vcpu/text()", document))
	}

}