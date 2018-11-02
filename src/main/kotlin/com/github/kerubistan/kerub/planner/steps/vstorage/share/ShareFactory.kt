package com.github.kerubistan.kerub.planner.steps.vstorage.share

import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.steps.StepFactoryCollection
import com.github.kerubistan.kerub.planner.steps.vstorage.mount.MountNfsFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.mount.UnmountNfsFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.IscsiShareFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs.ShareNfsFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs.UnshareNfsFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs.daemon.StartNfsDaemonFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs.daemon.StopNfsDaemonFactory

object ShareFactory : StepFactoryCollection(listOf(ShareNfsFactory, MountNfsFactory, UnmountNfsFactory,
												   UnshareNfsFactory, StartNfsDaemonFactory, StopNfsDaemonFactory,
												   IscsiShareFactory)) {
	override val expectationHints = super.expectationHints + VirtualMachineAvailabilityExpectation::class

}