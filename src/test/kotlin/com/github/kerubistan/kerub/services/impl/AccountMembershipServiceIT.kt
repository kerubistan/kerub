package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.RestException
import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.expect
import com.github.kerubistan.kerub.login
import com.github.kerubistan.kerub.model.Account
import com.github.kerubistan.kerub.model.AccountMembership
import com.github.kerubistan.kerub.runRestAction
import com.github.kerubistan.kerub.services.AccountMembershipService
import com.github.kerubistan.kerub.services.AccountService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.assertEquals

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
			assertThrows<RestException> {
				it.list(accountId)
			}
			assertThrows<RestException> {
				it.listUserAccounts("admin")
			}
			assertThrows<RestException> {
				val id = UUID.randomUUID()
				it.add(accountId, id, AccountMembership(
						id = id,
						groupId = accountId,
						user = "admin"
				))
			}
			assertThrows<RestException> {
				it.remove(accountId, UUID.randomUUID())
			}
		}
	}

	@Test
	fun security() {
		createClient().runRestAction(AccountMembershipService::class) {

			expect(RestException::class,
					action = { it.list(UUID.randomUUID()) },
					check = { assertEquals("AUTH1", it.code) }
			)

			expect(RestException::class,
					action = { it.remove(UUID.randomUUID(), UUID.randomUUID()) },
					check = { assertEquals("AUTH1", it.code) }
			)

			expect(RestException::class,
					action = { it.listUserAccounts("admin") },
					check = { assertEquals("AUTH1", it.code) }
			)

			expect(RestException::class,
					action = {
						it.add(UUID.randomUUID(), UUID.randomUUID(), AccountMembership("foo", UUID.randomUUID(), null))
					},
					check = { assertEquals("AUTH1", it.code) }
			)

		}
	}
}