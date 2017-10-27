package com.github.kerubistan.kerub.utils.junix.benchmarks.fio

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class IoResult @JsonCreator constructor(
		@JsonProperty("io_bytes") val ioBytes: Long,
		@JsonProperty("bw") val bandwidth: Long,
		@JsonProperty("iops") val iops: Float,
		@JsonProperty("total_ios") val totalIos: Long,
		@JsonProperty("slat") val submissionLatency: Latency,
		@JsonProperty("clat") val completionLatency: Latency
)