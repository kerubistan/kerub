package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.annotations.Dynamic
import com.github.kerubistan.kerub.model.history.IgnoreDiff
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field
import java.math.BigInteger
import java.util.UUID
import org.hibernate.search.annotations.Analyze.NO as noAnalyze

@JsonTypeName("vm-dyn")
@Dynamic(VirtualMachine::class)
data class VirtualMachineDynamic(
		@DocumentId
		@JsonProperty("id")
		override
		val id: UUID,
		@IgnoreDiff
		override val lastUpdated: Long = System.currentTimeMillis(),
		val hostId: UUID,
		@Field
		val status: VirtualMachineStatus = VirtualMachineStatus.Down,
		val memoryUsed: BigInteger,
		val cpuUsage: List<CpuStat> = listOf(),
		val displaySetting: DisplaySettings? = null,
		/**
		 * Note: instead of the unix/linux standard, where one can stick to a
		 * thread of a core, we will stick to a core only. The permission to
		 * run on a core should be translated by the executorsM to run on any
		 * threads of the core.
		 */
		val coreAffinity: List<Int>? = null
) : DynamicEntity {
	//TODO: issue #125 - workaround to allow infinispan query hostId
	@Field(analyze = noAnalyze)
	@JsonIgnore
	fun getHostIdStr() = hostId.toString()
}
