package com.github.kerubistan.kerub.utils.artemis

import com.github.kerubistan.kerub.host.ControllerManager
import com.github.kerubistan.kerub.utils.use
import org.apache.activemq.artemis.api.core.SimpleString
import org.apache.activemq.artemis.jms.server.embedded.EmbeddedJMS
import javax.jms.ConnectionFactory

/**
 * This class (and package) is only needed because HornetQ/Artemis does not support auto-creation of destinations.
 * https://issues.jboss.org/browse/HORNETQ-302
 */
object MqInit {
	@JvmStatic
	fun init(controllerManager: ControllerManager, artemis: EmbeddedJMS,
			 factory: ConnectionFactory): ConnectionFactory {
		val controllerId = controllerManager.getControllerId()
		val server = artemis.activeMQServer
		val mqName = "kerub-mq-$controllerId"
		server.deployQueue(SimpleString(mqName), SimpleString(mqName), null, true, false)
		factory.createConnection().use {
			it.createSession().use {
				it.createQueue(mqName)
			}
		}
		return factory
	}
}