package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi

import com.github.kerubistan.kerub.planner.steps.StepFactoryCollection
import com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.tgtd.TgtdIscsiUnshareFactory

object IscsiUnshareFactory : StepFactoryCollection(listOf(TgtdIscsiUnshareFactory))