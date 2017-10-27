package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.tgtd

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.AbstractIscsiShare

data class TgtdIscsiShare(override val host: Host, override val vstorage: VirtualStorageDevice, val devicePath: String) : AbstractIscsiShare {

	override fun toString() = "TgtdIscsiShare(host=${host.address} (${host.id})," +
			"vstorage=${vstorage.name} (${vstorage.id})," +
			"path=$devicePath)"
}