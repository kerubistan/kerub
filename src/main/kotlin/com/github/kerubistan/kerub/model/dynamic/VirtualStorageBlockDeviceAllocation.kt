package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat

interface VirtualStorageBlockDeviceAllocation : VirtualStorageAllocation {
	@get:JsonIgnore
	override val type: VirtualDiskFormat
		get() = VirtualDiskFormat.raw
}