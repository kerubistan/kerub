package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName
import org.hibernate.search.annotations.Field
import java.util.UUID

@JsonTypeName("account")
data class Account(
		override val id: UUID,
		@Field
		val name: String,
		val requireProjects: Boolean = false,
		val quota: Quota? = null
) : Entity<UUID>