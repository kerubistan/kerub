package com.github.K0zka.kerub.model

import java.util.UUID
import java.util.Date
import javax.xml.bind.annotation.XmlRootElement
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field

XmlRootElement(name = "project")
JsonTypeName("project")
JsonCreator
public class Project(
		DocumentId
		JsonProperty("id")
		override var id: UUID = UUID.randomUUID(),
		Field
		JsonProperty("name")
		val name: String,
		Field
		JsonProperty("description")
		val description: String,
		Field
		JsonProperty("created")
		val created: Date,
		Field
		JsonProperty("expectations")
		val expectations: List<Expectation> = serializableListOf()
                    )
: Entity<UUID>
