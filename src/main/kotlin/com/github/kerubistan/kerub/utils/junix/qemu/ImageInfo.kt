package com.github.kerubistan.kerub.utils.junix.qemu

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
data class ImageInfo(
		@JsonProperty("filename")
		val fileName: String,
		@JsonProperty("format")
		val format: VirtualDiskFormat,
		@JsonProperty("virtual-size")
		val virtualSize: Long,
		@JsonProperty("actual-size")
		val diskSize: Long,
		@JsonProperty("backing-filename")
		val backingFile: String?
) : Serializable