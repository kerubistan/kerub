package com.github.kerubistan.kerub.data.ispn

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.model.VirtualMachine
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.eq
import java.io.OutputStream
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JsonMarshallerTest {

	private val mapper: ObjectMapper = mock()

	private var marshaller: JsonMarshaller? = null

	private val testObject = VirtualMachine(
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

		verify(mapper)!!.writeValue(any(OutputStream::class.java), eq(testObject))
	}

	@Test
	fun objectFromByteBuffer() {
		whenever(mapper.readValue(
				any(ByteArray::class.java),
				anyInt(),
				anyInt(),
				eq(Entity::class.java))).thenReturn(testObject)

		assertEquals(testObject, marshaller!!.objectFromByteBuffer(ByteArray(0)))

		verify(mapper).readValue(
				any(ByteArray::class.java),
				anyInt(),
				anyInt(),
				eq(Entity::class.java))
	}

	@Test
	fun objectFromByteBufferWithLimits() {
		whenever(mapper.readValue(
				any(ByteArray::class.java),
				anyInt(),
				anyInt(),
				eq(Entity::class.java))).thenReturn(testObject)

		assertEquals(testObject, marshaller!!.objectFromByteBuffer(ByteArray(0), 0, 0))

		verify(mapper).readValue(
				any(ByteArray::class.java),
				anyInt(),
				anyInt(),
				eq(Entity::class.java))
	}

	@Test
	fun isMarshallable() {
		assertTrue(marshaller!!.isMarshallable(testObject))
		assertFalse(marshaller!!.isMarshallable("obviously not"))
	}
}