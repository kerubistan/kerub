package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.utils.junix.common.OsCommand

interface ServiceManager {
	fun start(service : OsCommand)
	fun stop(service : OsCommand)
	fun enable(service : OsCommand)
	fun disable(service : OsCommand)
}