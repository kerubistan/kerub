package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field
import java.util.*

@JsonTypeName("project")
public class Project @JsonCreator constructor (
		@DocumentId
        @JsonProperty("id")
		override var id: UUID = UUID.randomUUID(),
        @Field
        @JsonProperty("name")
		override val name: String,
        @Field
        @JsonProperty("description")
		val description: String,
        @Field
        @JsonProperty("created")
		val created: Date,
		override
        @Field
        @JsonProperty("expectations")
		val expectations: List<Expectation> = listOf()
                    )
: Entity<UUID>, Named, Constrained<Expectation>
