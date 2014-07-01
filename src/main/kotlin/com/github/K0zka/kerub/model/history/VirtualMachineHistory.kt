package com.github.K0zka.kerub.model.history

import java.util.UUID
import java.io.Serializable

public class VirtualMachineHistory(val key : HistoryKey<UUID>) : Serializable {
	var cpuLoad : Int = 0
	var memFree : Long = 0
}