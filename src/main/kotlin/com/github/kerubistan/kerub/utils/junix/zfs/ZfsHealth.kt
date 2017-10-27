package com.github.kerubistan.kerub.utils.junix.zfs

enum class ZfsHealth {
	online,
	degraded,
	faulted,
	offline,
	removed,
	unavail
}