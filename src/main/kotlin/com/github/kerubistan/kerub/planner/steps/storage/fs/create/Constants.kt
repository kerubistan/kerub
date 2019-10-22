package com.github.kerubistan.kerub.planner.steps.storage.fs.create

import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import io.github.kerubistan.kroki.size.KB

// known not supported: raw (obviously), vdi
internal val blankSize = mapOf(
		VirtualDiskFormat.qcow2 to 196.KB.toInt(),
		VirtualDiskFormat.qed to 320.KB.toInt(),
		VirtualDiskFormat.vmdk to 64.KB.toInt()
)
internal val formatsWithBaseImage = blankSize.keys.toSet()
