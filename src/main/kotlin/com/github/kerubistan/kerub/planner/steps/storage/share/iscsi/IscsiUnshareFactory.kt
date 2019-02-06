package com.github.kerubistan.kerub.planner.steps.storage.share.iscsi

import com.github.kerubistan.kerub.planner.steps.StepFactoryCollection
import com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.tgtd.TgtdIscsiUnshareFactory

object IscsiUnshareFactory : StepFactoryCollection(listOf(TgtdIscsiUnshareFactory))