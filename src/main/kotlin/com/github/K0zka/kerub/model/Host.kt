package com.github.K0zka.kerub.model

import java.util.UUID
import javax.xml.bind.annotation.XmlRootElement
import org.hibernate.search.annotations.Indexed
import org.hibernate.search.annotations.DocumentId
import com.fasterxml.jackson.annotation.JsonTypeName
import org.hibernate.search.annotations.Field
import com.fasterxml.jackson.annotation.JsonCreator
import org.codehaus.jackson.annotate.JsonProperty

/**
 *
 */
Indexed
XmlRootElement(name = "host")
JsonTypeName("host")
data class Host [JsonCreator] (
		override
		DocumentId
		JsonProperty("id")
		val id: UUID = UUID.randomUUID(),
		Field
		JsonProperty("address")
		val address: String,
		Field
		JsonProperty("publickey")
		val publicKey: String,
		Field
		JsonProperty("dedicated")
		val dedicated: Boolean,
		Field
		JsonProperty("capabilities")
		val capabilities: HostCapabilities? = null
               ) : Entity<UUID> {
}