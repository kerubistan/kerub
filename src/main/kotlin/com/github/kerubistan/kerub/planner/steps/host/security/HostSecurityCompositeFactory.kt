package com.github.kerubistan.kerub.planner.steps.host.security

import com.github.kerubistan.kerub.planner.steps.StepFactoryCollection
import com.github.kerubistan.kerub.planner.steps.host.security.clear.ClearSshKeyFactory
import com.github.kerubistan.kerub.planner.steps.host.security.generate.GenerateSshKeyFactory
import com.github.kerubistan.kerub.planner.steps.host.security.install.InstallPublicKeyFactory
import com.github.kerubistan.kerub.planner.steps.host.security.remove.RemovePublicKeyFactory

object HostSecurityCompositeFactory : StepFactoryCollection(
		listOf(
				ClearSshKeyFactory,
				GenerateSshKeyFactory,
				InstallPublicKeyFactory,
				RemovePublicKeyFactory
		)
)