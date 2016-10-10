package com.github.K0zka.kerub.utils.junix.zfs

enum class ZfsHealth {
	online,
	degraded,
	faulted,
	offline,
	removed,
	unavail
}