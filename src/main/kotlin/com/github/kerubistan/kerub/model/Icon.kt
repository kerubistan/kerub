package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.UUID

@JsonTypeName("icon")
data class Icon(
		override val id: UUID,
		val mediaType: String,
		val data: ByteArray
) : Entity<UUID> {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Icon

		if (id != other.id) return false
		if (mediaType != other.mediaType) return false
		if (!data.contentEquals(other.data)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + mediaType.hashCode()
		result = 31 * result + data.contentHashCode()
		return result
	}
}