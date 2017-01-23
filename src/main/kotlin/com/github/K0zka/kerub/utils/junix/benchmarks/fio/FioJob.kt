package com.github.K0zka.kerub.utils.junix.benchmarks.fio

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class FioJob @JsonCreator constructor(
		@JsonProperty("jobname") val jobName: String,
		@JsonProperty("read") val read: IoResult,
		@JsonProperty("write") val write: IoResult
)
