package com.github.kerubistan.kerub.utils.junix.mount

data class FsMount(
		val device: String,
		val mountPoint: String,
		val type: String,
		val options: List<String>
)