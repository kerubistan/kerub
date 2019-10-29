package com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.tgtd

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep
import com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.AbstractIscsiUnshare

@JsonTypeName("tgtd-iscsi-unshare")
data class TgtdIscsiUnshare(
		override val host: Host,
		override val vstorage: VirtualStorageDevice,
		override val allocation: VirtualStorageAllocation
) : AbstractIscsiUnshare, InvertibleStep {
	override fun isInverseOf(other: AbstractOperationalStep) =
			other is TgtdIscsiShare && other.isInverseOf(this)
}