package com.github.kerubistan.kerub.planner.steps.storage.share.iscsi

import com.github.kerubistan.kerub.planner.steps.StepFactoryCollection
import com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.ctld.CtldIscsiShareFactory
import com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.tgtd.TgtdIscsiShareFactory

object IscsiShareFactory : StepFactoryCollection(listOf(
		TgtdIscsiShareFactory,
		CtldIscsiShareFactory
), enabled = { it.storageTechnologies.iscsiEnabled })