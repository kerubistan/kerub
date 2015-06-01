package com.github.K0zka.kerub.model

import java.util.UUID
import javax.xml.bind.annotation.XmlRootElement
import org.hibernate.search.annotations.Indexed
import org.hibernate.search.annotations.DocumentId
import com.fasterxml.jackson.annotation.JsonTypeName
import org.hibernate.search.annotations.Field
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonView
import com.github.K0zka.kerub.model.views.Detailed
import com.github.K0zka.kerub.model.views.Simple
import org.codehaus.jackson.annotate.JsonProperty

/**
 *
 */
Indexed
XmlRootElement(name = "host")
JsonTypeName("host")
data class Host [JsonCreator] (
		override
		JsonView(javaClass<Simple>())
		DocumentId
		JsonProperty("id")
		val id: UUID = UUID.randomUUID(),
		Field
		JsonProperty("address")
		val address: String,
		JsonView(javaClass<Detailed>())
		Field
		JsonProperty("publickey")
		val publicKey: String,
		JsonView(javaClass<Simple>())
		Field
		JsonProperty("dedicated")
		val dedicated: Boolean,
		Field
		JsonProperty("capabilities")
		JsonView(javaClass<Detailed>())
		val capabilities: HostCapabilities? = null
                              )
: Entity<UUID>
