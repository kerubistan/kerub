package com.github.kerubistan.kerub.model.history

import java.math.BigDecimal

data class NumericPropertyChangeSummary(
		override
		val property: String,
		val min: Number,
		val max: Number,
		val average: BigDecimal,
		val extremes: List<Pair<Long, PropertyChange>>
) : PropertyChangeSummary