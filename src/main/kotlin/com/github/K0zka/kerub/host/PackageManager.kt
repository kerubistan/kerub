package com.github.K0zka.kerub.host

interface PackageManager {
	fun install(pack: String)
	fun remove(pack: String)
}