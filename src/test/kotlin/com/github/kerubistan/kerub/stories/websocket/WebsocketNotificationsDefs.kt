package com.github.kerubistan.kerub.stories.websocket

import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.login
import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.model.Pool
import com.github.kerubistan.kerub.model.messages.EntityAddMessage
import com.github.kerubistan.kerub.model.messages.EntityRemoveMessage
import com.github.kerubistan.kerub.model.messages.EntityUpdateMessage
import com.github.kerubistan.kerub.model.messages.Message
import com.github.kerubistan.kerub.model.messages.SubscribeMessage
import com.github.kerubistan.kerub.runRestAction
import com.github.kerubistan.kerub.services.HostService
import com.github.kerubistan.kerub.services.PoolService
import com.github.kerubistan.kerub.services.RestCrud
import com.github.kerubistan.kerub.services.VirtualMachineService
import com.github.kerubistan.kerub.services.VirtualNetworkService
import com.github.kerubistan.kerub.services.VirtualStorageDeviceService
import com.github.kerubistan.kerub.services.getServiceBaseUrl
import com.github.kerubistan.kerub.stories.entities.EntityDefs
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testVirtualNetwork
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.testWsUrl
import com.github.kerubistan.kerub.utils.createObjectMapper
import com.github.kerubistan.kerub.utils.getLogger
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import io.github.kerubistan.kroki.time.now
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import org.eclipse.jetty.websocket.client.WebSocketClient
import org.junit.After
import java.net.CookieManager
import java.net.HttpCookie
import java.net.URI
import java.util.UUID
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass
import kotlin.test.fail


class WebsocketNotificationsDefs {

	private var socketClient: WebSocketClient? = null
	private var session: Session? = null
	private val events: BlockingQueue<Any> = ArrayBlockingQueue<Any>(1024)

	private var entities = listOf<Any>()
	private var listener: Listener? = null

	data class ConnectEvent(val time: Long = now())

	data class DisconnectEvent(val time: Long = now())

	data class MessageEvent(val time: Long = now(), val message: Message)

	@WebSocket
	class Listener(private val messages: BlockingQueue<Any>) {

		companion object {
			private val mapper = createObjectMapper()
		}

		@OnWebSocketConnect
		fun connect(session: Session) {
			logger.info("connected: ${session.isOpen}")
			messages.put(ConnectEvent())
		}

		@OnWebSocketClose
		fun close(code: Int, msg: String?) {
			logger.info("connection closed {} {}", code, msg)
			messages.put(DisconnectEvent())
		}

		@OnWebSocketMessage
		fun message(session: Session, input: String) {
			logger.info("message: {}", input)
			val msg = mapper.readValue(input, Message::class.java)
			messages.add(MessageEvent(message = msg))
			logger.info("message: {}", msg)
		}

		@OnWebSocketError
		fun error(error: Throwable) {
			logger.info("socket error", error)
		}

	}

	companion object {
		private val logger = getLogger()
		private val mapper = createObjectMapper(prettyPrint = true)
	}

	@Given("(\\S+) is connected to websocket")
	fun connectToWebsocket(listenerUser: String) {
		val client = createClient()
		val response = client.login(username = listenerUser, password = "password")

		socketClient = WebSocketClient()

		socketClient!!.cookieStore = CookieManager().cookieStore
		response.cookies.forEach {
			socketClient!!.cookieStore.add(URI(getServiceBaseUrl()), HttpCookie(it.value.name, it.value.value))
		}
		socketClient!!.start()
		listener = Listener(events)
		session = socketClient!!.connect(listener, URI(testWsUrl)).get()

	}

	@Given("user subscribed to message feed (\\S+)")
	fun subscribeToMessageFeed(messageFeed: String) {
		session!!.remote.sendString(mapper.writeValueAsString(SubscribeMessage(channel = messageFeed)))
	}

	private val actions = mapOf<String, (RestCrud<Entity<UUID>>, Entity<UUID>) -> Any>(
			"creates" to { x, obj ->
				entities += x.add(obj)
				obj
			},
			"updates" to { x, obj -> x.update(obj.id, obj) },
			"deletes" to { x, obj ->
				x.delete(obj.id)
				EntityDefs.instance.entityDropped(obj)
			}
	)

	private val entityTypes = mapOf<String, KClass<*>>(
			"vm" to VirtualMachineService::class,
			"virtual network" to VirtualNetworkService::class,
			"virtual disk" to VirtualStorageDeviceService::class,
			"pool" to PoolService::class,
			"host" to HostService::class
	)

	@When("the user (\\S+) creates (vm|virtual network|virtual disk|pool)")
	fun runUserAdd(userName: String, entityType: String) {
		val client = createClient()
		client.login(userName, "password")
		when(entityType) {
			"vm" -> client.runRestAction(VirtualMachineService::class) {
				entities += it.add(testVm.copy(id= UUID.randomUUID()))
			}
			"virtual network" -> client.runRestAction(VirtualNetworkService::class) {
				entities += it.add(testVirtualNetwork.copy(id = UUID.randomUUID()))
			}
			"virtual disk" -> client.runRestAction(VirtualStorageDeviceService::class) {
				entities += it.add(testDisk.copy(id = UUID.randomUUID()))
			}
			"pool" -> client.runRestAction(PoolService::class) {
				entities += it.add(Pool(name = "test pool", templateId = UUID.randomUUID()))
			}
		}
	}

	@When("the user (\\S+) (updates|deletes) (vm|virtual network|virtual disk|pool) (\\S+)")
	fun runUserAction(userName: String, action: String, entityType: String, entityName: String) {
		val client = createClient()
		client.login(userName, "password")
		client.runRestAction(requireNotNull(entityTypes[entityType]) { "Unknown type: $entityType" }) {
			val actionFn = requireNotNull(actions[action]) {
				"Unhandled action: $action"
			}
			actionFn(it as RestCrud<Entity<UUID>>, EntityDefs.instance.getEntity(entityName))
		}
	}

	@Then("listener user must not receive socket notification( with type.*)?")
	fun checkNoNotification(type: String?) {
		logger.info("expecting NO message....")
		val expectedType = type?.substringAfter(" with type ")?.trim()
		var event = events.poll(100, TimeUnit.MILLISECONDS)
		while (event != null) {
			logger.info("event: {}", event)
			if (event is MessageEvent && matchesType(expectedType, event)) {
				fail("expected no ${expectedType ?: ""} message, got $event")
			}
			event = events.poll(100, TimeUnit.MILLISECONDS)
		}
	}

	@Then("listener user must receive socket notification( with type .*)?")
	fun checkNotification(type: String?) {
		val eventsReceived = mutableListOf<Any>()
		val expectedType = type?.substringAfter(" with type ")?.trim()
		logger.info("expecting message....")
		var event = events.poll(1000, TimeUnit.MILLISECONDS)
		while (event != null) {
			eventsReceived.add(event)
			logger.info("event: {}", event)

			if (event is MessageEvent && matchesType(expectedType, event)) {
				logger.info("pass...")
				return
			}
			event = events.poll(1000, TimeUnit.MILLISECONDS)
		}

		fail("expected ${expectedType ?: ""} message not received\n got instead: $eventsReceived")
	}

	private val eventTypes = mapOf(
			"update" to EntityUpdateMessage::class,
			"delete" to EntityRemoveMessage::class,
			"create" to EntityAddMessage::class
	)

	private fun matchesType(expectedType: String?, event: MessageEvent): Boolean =
			expectedType == null || event.message.javaClass.kotlin == eventTypes[expectedType]

	@After
	fun cleanup() {
		socketClient?.stop()
		val client = createClient()
		client.login("admin", "password")
		entities.forEach {
			entity ->
			when (entity) {
				is Entity<*> ->
					client.runRestAction(VirtualMachineService::class) {
						it.delete(entity.id as UUID)
					}
				else -> fail("can not clean up $entity")
			}
		}
	}
}