package com.github.kerubistan.kerub.data.alerts

fun Alert.validate() {
	if (open) {
		check(resolved == null) { "if the alert is still open, resolved must be null" }
	}
	if (!open) {
		check(resolved != null) { "if the alert is not open, resolution timestamp is required" }
		check(resolved!! > created) { "resolution must be after creation" }
	}
}