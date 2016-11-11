package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.login
import com.github.K0zka.kerub.runRestAction
import com.github.K0zka.kerub.services.ControllerConfigService

fun setAccountsRequired(accountsRequired : Boolean) {
	val client = createClient()
	client.login()
	client.runRestAction(ControllerConfigService::class) {
		it.set(
				it.get().copy(
						accountsRequired = accountsRequired
				)
		)
	}
}