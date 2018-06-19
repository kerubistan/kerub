package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.tgtd

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep
import com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.AbstractIscsiUnshare

data class TgtdIscsiUnshare(
		override val host: Host,
		override val vstorage: VirtualStorageDevice,
		override val allocation: VirtualStorageAllocation
) : AbstractIscsiUnshare, InvertibleStep {
	override fun isInverseOf(other: AbstractOperationalStep) =
			other is TgtdIscsiShare && other.isInverseOf(this)
}