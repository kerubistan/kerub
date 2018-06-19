package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.ctld

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.AbstractIscsiShare

data class CtldIscsiShare(
		override val host: Host,
		override val vstorage: VirtualStorageDevice,
		override val allocation: VirtualStorageAllocation
) : AbstractIscsiShare