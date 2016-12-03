package com.github.K0zka.kerub.stories.authorization

import com.github.K0zka.kerub.RestException
import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.login
import com.github.K0zka.kerub.model.Account
import com.github.K0zka.kerub.model.AccountMembership
import com.github.K0zka.kerub.model.Asset
import com.github.K0zka.kerub.model.AssetOwner
import com.github.K0zka.kerub.model.AssetOwnerType
import com.github.K0zka.kerub.model.ControllerConfig
import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.model.Named
import com.github.K0zka.kerub.model.VirtualNetwork
import com.github.K0zka.kerub.model.VirtualStorageLink
import com.github.K0zka.kerub.model.io.BusType
import com.github.K0zka.kerub.model.io.DeviceType
import com.github.K0zka.kerub.model.messages.SubscribeMessage
import com.github.K0zka.kerub.runRestAction
import com.github.K0zka.kerub.services.AccountMembershipService
import com.github.K0zka.kerub.services.AccountService
import com.github.K0zka.kerub.services.ControllerConfigService
import com.github.K0zka.kerub.services.RestCrud
import com.github.K0zka.kerub.services.RestOperations
import com.github.K0zka.kerub.services.VirtualMachineService
import com.github.K0zka.kerub.services.VirtualNetworkService
import com.github.K0zka.kerub.services.VirtualStorageDeviceService
import com.github.K0zka.kerub.socket.WebSocketSecurityIT
import com.github.K0zka.kerub.testDisk
import com.github.K0zka.kerub.testVm
import com.github.K0zka.kerub.testWsUrl
import com.github.K0zka.kerub.utils.createObjectMapper
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.skip
import com.github.K0zka.kerub.utils.substringBetween
import com.nhaarman.mockito_kotlin.mock
import cucumber.api.DataTable
import cucumber.api.java.After
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.apache.commons.io.input.NullInputStream
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import org.apache.cxf.jaxrs.client.WebClient
import org.eclipse.jetty.util.HttpCookieStore
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import org.eclipse.jetty.websocket.client.WebSocketClient
import java.net.CookieStore
import java.net.HttpCookie
import java.net.URI
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail


class AuthorizationDefs {

	var exception: RestException? = null

	var entities = mapOf<String, Entity<UUID>>()
	private var originalConfig: ControllerConfig? = null

	companion object {
		val logger = getLogger(AuthorizationDefs::class)
	}

	fun <X : Any> WebClient.tryRunRestAction(clientClass: KClass<X>, action: (X) -> Unit) {
		try {
			val serviceClient = JAXRSClientFactory.fromClient(this, clientClass.java)
			action(serviceClient)
		} catch (re: RestException) {
			exception = re
		}
	}

	private fun updateConfig(action: (ControllerConfig) -> ControllerConfig) {
		val client = createClient()
		client.login()
		client.runRestAction(ControllerConfigService::class) {
			val original = it.get()
			if (this.originalConfig == null) {
				this.originalConfig = original
			}
			it.set(action(original))
		}
	}

	var accounts: Map<String, Account> = mapOf()

	@Given("accounts:")
	fun createAccounts(table: DataTable) {
		val client = createClient()
		client.login()
		for (row in table.gherkinRows.skip()) {
			client.runRestAction(AccountService::class) {
				val account = it.add(
						Account(
								id = UUID.randomUUID(),
								name = row.cells[0]
						)
				)
				accounts += (account.name to account)
			}
		}
	}

	@Given("User (\\S+) is not member of any account")
	fun clearUserAccountMemberships(userName: String) {
		val client = createClient()
		client.login()
		client.runRestAction(AccountMembershipService::class) {
			accountMemberships ->
			accountMemberships.listUserAccounts(userName).forEach {
				accountMemberships.remove(it.groupId, it.id)
			}
		}
	}

	@Given("There are VMs defined:")
	fun defineVms(dataTable: DataTable) {
		val client = createClient()
		client.login()
		client.runRestAction(VirtualMachineService::class) {
			vms ->
			dataTable.gherkinRows.skip().forEach {
				row ->
				val vmName = row.cells[0]
				val vm = vms.add(testVm.copy(
						id = UUID.randomUUID(),
						name = vmName,
						owner = AssetOwner(
								ownerId = requireNotNull(accounts[row.cells[1]]).id,
								ownerType = AssetOwnerType.account
						)
				))
				entities = entities + (vmName to vm)
			}
		}
	}

	@Given("User (\\S+) is member of (.*)")
	fun addUserToAccount(userName: String, accountName: String) {
		val client = createClient()
		client.login()
		client.runRestAction(AccountMembershipService::class) {
			val membership = AccountMembership(
					user = userName,
					groupId = accounts[accountName]!!.id
			)

			it.add(membership.groupId, membership.id, membership)
		}

	}

	@Given("There are disks defined:")
	fun defineDisks(dataTable: DataTable) {
		val client = createClient()
		client.login()
		client.runRestAction(VirtualStorageDeviceService::class) {
			storageService ->
			dataTable.gherkinRows.skip().forEach {
				row ->
				val diskName = row.cells[0]
				val disk = storageService.add(testDisk.copy(
						id = UUID.randomUUID(),
						name = diskName,
						owner = AssetOwner(
								ownerId = requireNotNull(accounts[row.cells[1]]).id,
								ownerType = AssetOwnerType.account
						)
				)
				)
				entities = entities + (diskName to disk)
			}
		}
	}

	@Given("There are networks defined:")
	fun defineNetworks(dataTable: DataTable) {
		val client = createClient()
		client.login()
		client.runRestAction(VirtualNetworkService::class) {
			networkService ->
			dataTable.gherkinRows.skip().forEach {
				row ->
				val network = networkService.add(
						VirtualNetwork(
								id = UUID.randomUUID(),
								name = row.cells[0],
								owner = AssetOwner(
										ownerId = requireNotNull(accounts[row.cells[1]]).id,
										ownerType = AssetOwnerType.account
								)
						)
				)
				entities = entities + (network.name to network)
			}
		}
	}

	@When("Accounts are required")
	fun setAccountsRequired() {
		updateConfig() {
			it.copy(accountsRequired = true)
		}
	}

	@Then("User (\\S+) is not able to see the account (.*)")
	fun checkUserDoesNotSeeAccount(userName: String, accountName: String) {
		val client = createClient()
		client.login(userName, "password")
		client.tryRunRestAction(AccountService::class) {
			it.getById(requireNotNull(accounts[accountName]).id)
			fail("expected exception")
		}
	}

	@Then("User (\\S+) is not able to create vm outside of accounts")
	fun checkUserNoVmWithoutAccount(userName: String) {
		val client = createClient()
		client.login(userName, "password")
		client.tryRunRestAction(VirtualMachineService::class) {
			it.add(
					testVm.copy(
							id = UUID.randomUUID(),
							owner = null
					)
			)
			fail("expected exception")
		}
	}

	@Then("User (\\S+) is not able to create virtual network outside of accounts")
	fun checkUserNoVNetWithoutAccount(userName: String) {
		val client = createClient()
		client.login(userName, "password")
		client.tryRunRestAction(VirtualNetworkService::class) {
			val id = UUID.randomUUID()
			it.add(
					VirtualNetwork(
							id = id,
							name = "test-$id",
							owner = null
					)
			)
			fail("expected exception")
		}

	}

	@Then("User (\\S+) is not able to create virtual disk outside of accounts")
	fun checkUserNoVStorageWithoutAccount(userName: String) {
		val client = createClient()
		client.login(userName, "password")
		client.tryRunRestAction(VirtualStorageDeviceService::class) {
			val id = UUID.randomUUID()
			it.add(
					testDisk.copy(id = UUID.randomUUID())
			)
			fail("expected exception")
		}

	}

	@Then("User (\\S+) is able to see the account (.*)")
	fun checkUserisAbleToSeeAccount(userName: String, accountName: String) {
		val client = createClient()
		client.login(username = userName, password = "password")
		client.runRestAction(AccountService::class) {
			val expected = accounts[accountName]
			val account = it.getById(requireNotNull(expected).id)
			assertEquals(expected, account)
		}
	}

	@Then("user (\\S+) is able to create vm under account (.*)")
	fun checkUserCanCreateVm(userName: String, accountName: String) {
		val client = createClient()
		client.login(username = userName, password = "password")
		client.runRestAction(VirtualMachineService::class) {
			it.add(
					testVm.copy(
							id = UUID.randomUUID(),
							name = "vm under account ${accountName}",
							owner = AssetOwner(
									ownerId = requireNotNull(accounts[accountName]).id,
									ownerType = AssetOwnerType.account
							)
					)
			)
		}
	}

	@Then("user (\\S+) is able to create virtual network under account (.*)")
	fun checkUserCanCreateVNet(userName: String, accountName: String) {
		val client = createClient()
		client.login(username = userName, password = "password")
		client.runRestAction(VirtualNetworkService::class) {
			it.add(
					VirtualNetwork(
							id = UUID.randomUUID(),
							name = "vnet under account ${accountName}",
							owner = AssetOwner(
									ownerId = requireNotNull(accounts[accountName]).id,
									ownerType = AssetOwnerType.account
							)
					)
			)
		}
	}

	@Then("user (\\S+) is able to create virtual disk under account (.*)")
	fun checkUserCanCreateVStorage(userName: String, accountName: String) {
		val client = createClient()
		client.login(username = userName, password = "password")
		client.runRestAction(VirtualStorageDeviceService::class) {
			it.add(
					testDisk.copy(
							id = UUID.randomUUID(),
							name = "disk under account ${accountName}",
							owner = AssetOwner(
									ownerId = requireNotNull(accounts[accountName]).id,
									ownerType = AssetOwnerType.account
							)
					)
			)
		}
	}

	val actions = mapOf<String, (RestCrud<Entity<UUID>>, Entity<UUID>) -> Any>(
			"add" to { x, obj -> x.add(obj) },
			"see" to { x, obj -> x.getById(obj.id) },
			"list" to {
				x, obj ->
				val list = (x as RestOperations.List<*>).listAll(
						start = 0,
						limit = Int.MAX_VALUE,
						sort = "name"
				)
				if (list.result.none {
					(it as Map<String, String>)["id"] == obj.id.toString()
				}) {
					throw Exception("not found: ${obj.id}")
				}
			},
			"update" to { x, obj -> x.update(obj.id, obj) },
			"remove" to { x, obj -> x.delete(obj.id) },
			"start" to { x, obj ->
				(x as VirtualMachineService).startVm(obj.id)
			},
			"stop" to { x, obj -> (x as VirtualMachineService).stopVm(obj.id) },
			"upload" to {
				x, obj -> (x as VirtualStorageDeviceService)
					.load(id = obj.id, data = NullInputStream(0), async = mock())
			},
			"search" to {
				x, obj ->
				require(
						(x as RestOperations.SimpleSearch<Entity<UUID>>).search(
								field = "name",
								value = (obj as Named).name
						).result.any { it.id == obj.id }
				)
			}
	)

	val clients = mapOf<String, KClass<RestCrud<Entity<UUID>>>>(
			"vm" to VirtualMachineService::class as KClass<RestCrud<Entity<UUID>>>,
			"virtual disk" to VirtualStorageDeviceService::class as KClass<RestCrud<Entity<UUID>>>,
			"virtual network" to VirtualNetworkService::class as KClass<RestCrud<Entity<UUID>>>
	)

	@Then("User (\\S+) is (not)?\\s?able to (see|list|update|remove|start|stop|upload|search) (vm|virtual disk|virtual network) (\\S+)")
	fun checkAccess(userName: String, able: String?, actionName: String, objectType: String, objectName: String) {
		val shouldFail = able == "not"
		val client = createClient()
		client.login(username = userName, password = "password")

		val clientClass = requireNotNull(clients[objectType]) { "client class not found for '$objectType'" }
		val obj = requireNotNull(entities[objectName]) { "object $objectName not found" }
		val action = requireNotNull(actions[actionName]) { "action not found for name '$actionName'" }
		client.runRestAction(clientClass) {
			try {
				action(it, obj)
				if (shouldFail) {
					fail("should have failed")
				}
			} catch (e: Exception) {
				if (!shouldFail) {
					logger.error("unexpected exception ", e)
					fail("shouldn't have failed, $e")
				}
			}
		}

	}

	@Then("User (\\S+) is (not)?\\s+able to subscribe (vm|virtual disk|virtual network) (\\S+)")
	fun checkSocketAccess(userName: String, able : String, type: String, objectName : String) {
		val client = createClient()
		val resp = client.login(userName, "password")
		val wsClient = WebSocketClient()
		wsClient.start()
		val cookieStore : CookieStore = HttpCookieStore()
		wsClient.cookieStore = cookieStore
		resp.metadata["Set-Cookie"]?.forEach {
			val cookie = it.toString()
			wsClient.cookieStore.add( URI(testWsUrl), HttpCookie(cookie.substringBefore("="), cookie.substringBetween("=", ";")) )
		}

		@WebSocket
		class Listener {

			@Volatile
			var inbox = listOf<String>()

			@OnWebSocketConnect
			fun connect(session : Session) {
				WebSocketSecurityIT.logger.info("connected: ${session.isOpen}")
			}
			@OnWebSocketClose
			fun close(code: Int, msg : String?) {
				WebSocketSecurityIT.logger.info("connection closed {} {}", code, msg)
			}
			@OnWebSocketMessage
			fun message(session : Session, input : String) {
				WebSocketSecurityIT.logger.info("message: {}", input)
			}
			@OnWebSocketError
			fun error(error: Throwable) {
				WebSocketSecurityIT.logger.info("socket error", error)
			}

		}

		val obj = requireNotNull(entities[objectName])
		wsClient.connect(Listener(), URI(testWsUrl)).get().use {
			session ->
			session.remote.sendString(createObjectMapper().writeValueAsString(
					SubscribeMessage(
							channel = "/vm/"
					)
			))
			session.remote.sendString(createObjectMapper().writeValueAsString(
					SubscribeMessage(
							channel = "/vm/${obj.id}"
					)
			))
		}

	}

	@Then("(\\S+) is (not)? able to create vm with disk (\\S+) in account (.*)")
	fun checkUserCantAttach(userName: String, able: String, diskName: String, accountName: String) {
		val client = createClient()
		val shouldFail = able == "not"
		client.login(username = userName, password = "password")
		try {
			client.runRestAction(VirtualMachineService::class) {
				it.add(
						testVm.copy(
								id = UUID.randomUUID(),
								owner = AssetOwner(
										ownerType = AssetOwnerType.account,
										ownerId = requireNotNull(accounts[accountName]).id
								),
								virtualStorageLinks = listOf(
										VirtualStorageLink(
												bus = BusType.sata,
												device = DeviceType.disk,
												virtualStorageId = requireNotNull(this.entities[diskName]).id
										)
								)
						)
				)
			}
			assertFalse("$userName should not be able to create a vm with disk $diskName in account $accountName") { shouldFail }
		} catch (e: RestException) {
			assertTrue("$userName should be able to create a vm with disk $diskName in account $accountName") { shouldFail }
		}

	}

	@After
	fun cleanup() {
		val client = createClient()
		client.login()
		if (originalConfig != null) {
			client.runRestAction(ControllerConfigService::class) {
				it.set(originalConfig!!)
			}
		}
		client.runRestAction(AccountService::class) {
			accountService ->
			accounts.forEach {
				accountService.delete(it.value.id)
			}
		}
	}

}
