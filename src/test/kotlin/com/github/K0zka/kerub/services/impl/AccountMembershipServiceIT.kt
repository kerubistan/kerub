package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.RestException
import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.expect
import com.github.K0zka.kerub.login
import com.github.K0zka.kerub.model.Account
import com.github.K0zka.kerub.model.AccountMembership
import com.github.K0zka.kerub.runRestAction
import com.github.K0zka.kerub.services.AccountMembershipService
import com.github.K0zka.kerub.services.AccountService
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.UUID

class AccountMembershipServiceIT {

	var account: Account? = null

	@Before
	fun setup() {
		val client = createClient()
		client.login()

		account = client.runRestAction(AccountService::class) {
			it.add(Account(
					id = UUID.randomUUID(),
					name = "account membership test",
					requireProjects = false
			))
		}

	}

	@After
	fun cleanup() {
		val client = createClient()
		client.login()

		client.runRestAction(AccountService::class) {
			it.delete(account!!.id)
		}

	}

	@Test
	fun checkAuthentication() {
		val accountId = account!!.id
		createClient().runRestAction(AccountMembershipService::class) {
			expect(RestException::class) {
				it.list(accountId)
			}
			expect(RestException::class) {
				it.listUserAccounts("admin")
			}
			expect(RestException::class) {
				val id = UUID.randomUUID()
				it.add(accountId, id, AccountMembership(
						id = id,
						groupId = accountId,
						user = "admin"
				))
			}
			expect(RestException::class) {
				it.remove(accountId, UUID.randomUUID())
			}
		}
	}
}