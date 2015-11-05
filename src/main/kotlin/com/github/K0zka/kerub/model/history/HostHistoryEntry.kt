package com.github.K0zka.kerub.model.history

import java.util.UUID

public class HostHistoryEntry(
		val key: HistoryKey<UUID>,
		var cpuLoad: Int = 0,
		var memFree: Long = 0
) {
}