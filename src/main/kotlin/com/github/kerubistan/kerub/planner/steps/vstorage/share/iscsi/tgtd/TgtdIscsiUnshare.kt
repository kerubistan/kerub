package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.tgtd

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertableStep
import com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.AbstractIscsiUnshare

data class TgtdIscsiUnshare(override val host: Host, override val vstorage: VirtualStorageDevice) :
		AbstractIscsiUnshare, InvertableStep {
	override val inverseMatcher: (AbstractOperationalStep) -> Boolean
		get() = { it is TgtdIscsiShare && it.inverseMatcher.invoke(this) }
}