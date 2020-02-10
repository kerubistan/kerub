package com.github.kerubistan.kerub.security

import com.github.kerubistan.kerub.controller.InterController
import com.github.kerubistan.kerub.model.messages.SessionEventMessage
import com.github.kerubistan.kerub.utils.getLogger
import org.springframework.web.context.support.WebApplicationContextUtils
import javax.servlet.http.HttpSessionEvent
import javax.servlet.http.HttpSessionListener

class UserSessionListener : HttpSessionListener {

	companion object {
		private val logger = getLogger()
	}

	override fun sessionDestroyed(se: HttpSessionEvent) {
		val applicationContext = WebApplicationContextUtils.getWebApplicationContext(se.session.servletContext)
		val interController: InterController =
				applicationContext.getBean("interController", InterController::class.java)
		interController.broadcast(SessionEventMessage(closed = true, sessionId = se.session.id))
		logger.info("session destroy: {}", se.session.id)
	}

	override fun sessionCreated(se: HttpSessionEvent) {
		logger.debug("session created: {}", se.session.id)
	}
}