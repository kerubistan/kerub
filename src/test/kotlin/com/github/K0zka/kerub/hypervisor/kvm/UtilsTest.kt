package com.github.K0zka.kerub.hypervisor.kvm

import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.utils.getLogger
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import org.xml.sax.InputSource
import java.io.StringReader
import java.math.BigInteger
import java.util.UUID
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathFactory
import com.github.K0zka.kerub.model.Range as serializableRange

class UtilsTest {

	companion object val logger = getLogger(UtilsTest::class)

	@Test fun vmDefinitiontoXml() {
		val vm = VirtualMachine(
				id = UUID.randomUUID(),
				nrOfCpus = 2,
				expectations = listOf(),
				memory = serializableRange( BigInteger("1024"), BigInteger("2048")),
				name = "test-vm</",
				virtualStorageLinks = listOf()
		                       )

		val libvirtXml = vmDefinitiontoXml(vm, mapOf())

		val dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(InputSource(StringReader(libvirtXml)))
		val xPath = XPathFactory.newInstance().newXPath()

		logger.info("generated libvirt xml:\n{}", libvirtXml)
		Assert.assertThat(UUID.fromString(xPath.evaluate("domain/uuid/text()", dom)), CoreMatchers.`is`(vm.id))
		//domain name is from id, since it needs to be cluster-wide unique
		Assert.assertThat(UUID.fromString(xPath.evaluate("domain/name/text()", dom)), CoreMatchers.`is`(vm.id))
		Assert.assertThat(xPath.evaluate("domain/memory/text()", dom), CoreMatchers.`is`(vm.memory.min.toString()))

	}
}