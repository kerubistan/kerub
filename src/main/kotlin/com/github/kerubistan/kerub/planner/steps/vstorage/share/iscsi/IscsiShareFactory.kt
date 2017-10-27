package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi

import com.github.kerubistan.kerub.planner.steps.StepFactoryCollection
import com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.ctld.CtldIscsiShareFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.tgtd.TgtdIscsiShareFactory

object IscsiShareFactory : StepFactoryCollection(listOf(
		TgtdIscsiShareFactory,
		CtldIscsiShareFactory
))