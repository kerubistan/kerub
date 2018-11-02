package com.github.kerubistan.kerub.planner.steps.host.security

import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.issues.problems.hosts.RecyclingHost
import com.github.kerubistan.kerub.planner.steps.StepFactoryCollection
import com.github.kerubistan.kerub.planner.steps.host.security.clear.ClearSshKeyFactory
import com.github.kerubistan.kerub.planner.steps.host.security.generate.GenerateSshKeyFactory
import com.github.kerubistan.kerub.planner.steps.host.security.install.InstallPublicKeyFactory
import com.github.kerubistan.kerub.planner.steps.host.security.remove.RemovePublicKeyFactory
import kotlin.reflect.KClass

object HostSecurityCompositeFactory : StepFactoryCollection(
		listOf(
				ClearSshKeyFactory,
				GenerateSshKeyFactory,
				InstallPublicKeyFactory,
				RemovePublicKeyFactory
		)
) {
	override val problemHints: Set<KClass<out Problem>> = super.problemHints + RecyclingHost::class
}