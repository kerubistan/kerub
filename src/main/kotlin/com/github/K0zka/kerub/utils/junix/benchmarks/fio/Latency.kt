package com.github.K0zka.kerub.utils.junix.benchmarks.fio

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class Latency @JsonCreator constructor(
		@JsonProperty("min") val min: Int,
		@JsonProperty("max") val max: Int,
		@JsonProperty("mean") val mean: Float,
		@JsonProperty("stddev") val stdDev: Float,
		@JsonProperty("percentile") val percentiles: Map<BigDecimal, Int> = mapOf()
)