package com.github.K0zka.kerub.data.ispn

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.model.VirtualMachine
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import java.io.OutputStream
import java.util.UUID

class JsonMarshallerTest {

	val mapper: ObjectMapper = mock()

	var marshaller: JsonMarshaller? = null

	val testObject = VirtualMachine(
			id = UUID.randomUUID(),
			name = "foo"
	)

	@Before
	fun setup() {
		marshaller = JsonMarshaller(mapper)
	}

	@Test
	fun objectToByteBuffer() {
		marshaller!!.objectToByteBuffer(testObject)

		Mockito.verify(mapper)!!.writeValue(any(OutputStream::class.java), eq(testObject))
	}

	@Test
	fun objectFromByteBuffer() {
		Mockito.`when`(mapper.readValue(
				any(ByteArray::class.java),
				anyInt(),
				anyInt(),
				eq(Entity::class.java))).thenReturn(testObject)

		Assert.assertEquals(testObject, marshaller!!.objectFromByteBuffer(ByteArray(0)))

		Mockito.verify(mapper).readValue(
				any(ByteArray::class.java),
				anyInt(),
				anyInt(),
				eq(Entity::class.java))
	}

	@Test
	fun objectFromByteBufferWithLimits() {
		Mockito.`when`(mapper.readValue(
				any(ByteArray::class.java),
				anyInt(),
				anyInt(),
				eq(Entity::class.java))).thenReturn(testObject)

		Assert.assertEquals(testObject, marshaller!!.objectFromByteBuffer(ByteArray(0), 0, 0))

		Mockito.verify(mapper).readValue(
				any(ByteArray::class.java),
				anyInt(),
				anyInt(),
				eq(Entity::class.java))
	}

	@Test
	fun isMarshallable() {
		Assert.assertTrue(marshaller!!.isMarshallable(testObject))
		Assert.assertFalse(marshaller!!.isMarshallable("obviously not"))
	}
}