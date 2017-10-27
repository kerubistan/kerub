package com.github.kerubistan.kerub.host

import com.github.kerubistan.kerub.utils.junix.common.OsCommand

interface ServiceManager {
	fun start(service: OsCommand)
	fun stop(service: OsCommand)
	fun enable(service: OsCommand)
	fun disable(service: OsCommand)
}