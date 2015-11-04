package com.github.K0zka.kerub.data.ispn

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.utils.createObjectMapper
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import java.util.UUID

public class JsonMarshallerIT {

	val mapper: ObjectMapper = createObjectMapper()

	val vm = VirtualMachine(
			id = UUID.randomUUID(),
			name = "foo"
	                       )

	@Test
	fun objectToBuffer() {
		val buffer = JsonMarshaller(mapper).objectToBuffer(vm)
		Assert.assertThat("Buffer must be non-empty", buffer!!.getLength(), CoreMatchers.not(0))
	}

	@Test
	fun vmFromByteBuffer() {
		val vm = JsonMarshaller(mapper).objectFromByteBuffer(
				("""
				{
					"@type":"vm",
					"id":"49c4364b-75c5-4c16-9d6d-984d20a7356e",
					"name":"foo",
					"nrOfCpus":1,
					"memory":{
						"min":1024,
						"max":2048
						},
					"expectations":[],
					"storagedevices":[]
				}
				""").toByteArray("UTF-8")
		                                                    )
				as VirtualMachine
		Assert.assertThat("Person must be non-null", vm, CoreMatchers.notNullValue())
		Assert.assertThat("First name must match", vm.name, CoreMatchers.`is`("foo"))
	}

	@Test
	fun hostFromByteBuffer() {
		val host = JsonMarshaller(mapper).objectFromByteBuffer(
				("""
				{
					"@type":"host",
					"id":"49c4364b-75c5-4c16-9d6d-984d20a7356e",
					"address":"127.0.0.1",
					"dedicated":false,
					"publicKey":"abcdef"
				}
				""").toByteArray("UTF-8")
		                                                    )
				as Host
		Assert.assertThat("Person must be non-null", host, CoreMatchers.notNullValue())
		Assert.assertThat("Host address must match", host.address, CoreMatchers.`is`("127.0.0.1"))
		Assert.assertThat("Host public key must match", host.publicKey, CoreMatchers.`is`("abcdef"))
		Assert.assertThat("Host id must match", host.id, CoreMatchers.`is`(UUID.fromString("49c4364b-75c5-4c16-9d6d-984d20a7356e")))
	}

}