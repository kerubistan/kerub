package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.tgtd

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory

object TgtdIscsiUnshareFactory : AbstractOperationalStepFactory<TgtdIscsiUnshare>() {
	override fun produce(state: OperationalState): List<TgtdIscsiUnshare> {
		TODO()
	}
}