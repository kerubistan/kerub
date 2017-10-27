package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.login
import com.github.kerubistan.kerub.runRestAction
import com.github.kerubistan.kerub.services.ControllerConfigService

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