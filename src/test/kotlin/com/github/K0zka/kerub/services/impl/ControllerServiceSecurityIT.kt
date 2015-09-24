package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.security.Roles
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.platform.platformStatic

@RunWith(Parameterized::class)
public class ControllerServiceSecurityIT(val role : Roles, access : Boolean) {
	companion object {
		@Parameterized.Parameters
		platformStatic fun parameters() : List<Array<Any>> {
			return listOf(
					arrayOf<Any>(Roles.admin, true),
					arrayOf<Any>(Roles.powerUser, false),
					arrayOf<Any>(Roles.user, false)
			             )
		}
	}

	@Test
	fun check() {

	}

}