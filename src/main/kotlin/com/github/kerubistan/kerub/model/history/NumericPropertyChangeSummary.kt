package com.github.kerubistan.kerub.model.history

import com.fasterxml.jackson.annotation.JsonTypeName
import java.math.BigDecimal

@JsonTypeName("number-change")
data class NumericPropertyChangeSummary(
		override
		val property: String,
		val min: Number,
		val max: Number,
		val average: BigDecimal,
		val extremes: List<Pair<Long, PropertyChange>>
) : PropertyChangeSummary