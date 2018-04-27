package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.tgtd

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep
import com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.AbstractIscsiShare

data class TgtdIscsiShare(override val host: Host, override val vstorage: VirtualStorageDevice,
						  val devicePath: String) : AbstractIscsiShare, InvertibleStep {
	override fun isInverseOf(other: AbstractOperationalStep) =
			other is TgtdIscsiUnshare && other.host == host && other.vstorage == vstorage

	override fun toString() = "TgtdIscsiShare(host=${host.address} (${host.id})," +
			"vstorage=${vstorage.name} (${vstorage.id})," +
			"path=$devicePath)"
}