package com.github.kerubistan.kerub.model.history

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(NumericPropertyChangeSummary::class),
		JsonSubTypes.Type(DataPropertyChangeSummary::class),
		JsonSubTypes.Type(ListPropertyChangeSummary::class),
		JsonSubTypes.Type(EnumChangeSummary::class)
)
interface PropertyChangeSummary : Serializable {
	val property: String
}