package com.github.kerubistan.kerub.planner.steps.storage.share

import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.steps.StepFactoryCollection
import com.github.kerubistan.kerub.planner.steps.storage.mount.MountNfsFactory
import com.github.kerubistan.kerub.planner.steps.storage.mount.UnmountNfsFactory
import com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.IscsiShareFactory
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.ShareNfsFactory
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.UnshareNfsFactory
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.daemon.StartNfsDaemonFactory
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.daemon.StopNfsDaemonFactory

object ShareFactory : StepFactoryCollection(listOf(ShareNfsFactory, MountNfsFactory, UnmountNfsFactory,
												   UnshareNfsFactory, StartNfsDaemonFactory, StopNfsDaemonFactory,
												   IscsiShareFactory)) {
	override val expectationHints = super.expectationHints + VirtualMachineAvailabilityExpectation::class

}