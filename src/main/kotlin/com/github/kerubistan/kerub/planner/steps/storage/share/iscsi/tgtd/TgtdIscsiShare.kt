package com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.tgtd

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep
import com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.AbstractIscsiShare

data class TgtdIscsiShare(override val host: Host,
						  override val vstorage: VirtualStorageDevice,
						  override val allocation : VirtualStorageAllocation
) : AbstractIscsiShare, InvertibleStep {
	override fun isInverseOf(other: AbstractOperationalStep) =
			other is TgtdIscsiUnshare && other.host == host
					&& other.vstorage == vstorage
					&& other.allocation == allocation

	override fun toString() = "TgtdIscsiShare(host=${host.address} (${host.id})," +
			"vstorage=${vstorage.name} (${vstorage.id})," +
			"path=${allocation.getPath(vstorage.id)})"
}