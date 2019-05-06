package com.github.kerubistan.kerub.utils.junix.procfs

import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.utils.junix.common.OsCommand

interface AbstractProcFs : OsCommand {
	override fun available(hostCapabilities: HostCapabilities?): Boolean =
			hostCapabilities?.os == OperatingSystem.Linux
}