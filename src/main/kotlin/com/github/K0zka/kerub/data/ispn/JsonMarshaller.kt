package com.github.K0zka.kerub.data.ispn

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.controller.Assignment
import com.github.K0zka.kerub.utils.getLogger
import org.infinispan.commons.io.ByteBuffer
import org.infinispan.commons.io.ByteBufferImpl
import org.infinispan.commons.marshall.BufferSizePredictor
import org.infinispan.commons.marshall.Marshaller
import java.io.ByteArrayOutputStream

/**
 * Integrates the jackson objectmapper to the infinispan infrastructure and allows
 * persisting the data in json format.
 */
class JsonMarshaller(private val objectMapper: ObjectMapper) : Marshaller {

	object Predictor : BufferSizePredictor {
		override fun nextSize(obj: Any?): Int =
				when (obj) {
					is VirtualMachine -> 8 * KB
					is Assignment -> 1 * KB
					is Host -> 128 * KB
					else -> 1024
				}

		override fun recordSize(previousSize: Int) {
			logger.debug("size: $previousSize")
		}
	}

	companion object {
		val KB = 1024
		private val logger = getLogger(JsonMarshaller::class)
	}

	internal fun objectToByteArray(o: Any?, objectMapper: ObjectMapper, estimatedSize: Int? = null): ByteArray {
		return ByteArrayOutputStream(estimatedSize ?: 1024).use {
			objectMapper.writeValue(it, o)
			it.toByteArray()
		}
	}

	internal fun byteArrayToObject(objectMapper: ObjectMapper, bytes: ByteArray, start: Int? = null, len: Int? = null): Any? {
		return objectMapper.readValue(bytes, start ?: 1, len ?: bytes.size, Entity::class.java)
	}

	override fun objectToBuffer(o: Any?): ByteBuffer? {
		val bytes = objectToByteArray(o, objectMapper)
		return ByteBufferImpl(bytes, 0, bytes.size)
	}

	override fun objectToByteBuffer(obj: Any?, estimatedSize: Int): ByteArray
			= objectToByteArray(obj, objectMapper, estimatedSize)

	override fun objectToByteBuffer(obj: Any?): ByteArray
			= objectToByteArray(obj, objectMapper)

	override fun isMarshallable(o: Any?): Boolean = Entity::class.java.isAssignableFrom(o?.javaClass)

	override fun objectFromByteBuffer(buf: ByteArray?): Any?
			= byteArrayToObject(objectMapper, buf ?: ByteArray(0))

	override fun objectFromByteBuffer(buf: ByteArray?, offset: Int, length: Int): Any?
			= byteArrayToObject(objectMapper, buf ?: ByteArray(0), offset, length)

	override fun getBufferSizePredictor(o: Any?): BufferSizePredictor
			= Predictor
}