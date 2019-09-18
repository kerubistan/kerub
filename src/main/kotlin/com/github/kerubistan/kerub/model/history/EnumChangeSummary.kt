package com.github.kerubistan.kerub.model.history

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("enum-change")
data class EnumChangeSummary(
		override val property: String
) : PropertyChangeSummary