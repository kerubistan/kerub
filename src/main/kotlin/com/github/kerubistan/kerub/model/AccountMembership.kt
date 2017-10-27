package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import org.hibernate.search.annotations.Analyze
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field
import org.hibernate.search.annotations.Index
import org.hibernate.search.annotations.Indexed
import org.hibernate.search.annotations.Store
import java.util.UUID

@Indexed
@JsonTypeName("account-membership")
data class AccountMembership(
		@Field(analyze = Analyze.NO)
		override val user: String,
		@Field(analyze = Analyze.NO)
		override val groupId: UUID,
		override val quota: Quota? = null,
		@DocumentId
		override val id: UUID = UUID.randomUUID()
) : GroupMembership {
	val groupIdStr: String
		@Field(store = Store.YES, index = Index.YES, analyze = Analyze.NO)
		@JsonIgnore
		get() = groupId.toString()
}