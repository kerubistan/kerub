package com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.ctld

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.AbstractIscsiShare

@JsonTypeName("ctld-iscsi-share")
data class CtldIscsiShare(
		override val host: Host,
		override val vstorage: VirtualStorageDevice,
		override val allocation: VirtualStorageAllocation
) : AbstractIscsiShare