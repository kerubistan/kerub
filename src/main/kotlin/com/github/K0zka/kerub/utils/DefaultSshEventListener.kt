package com.github.K0zka.kerub.utils

import org.apache.sshd.common.session.Session
import org.apache.sshd.common.session.SessionListener

/**
 * Blank implementation of sshd session listener
 */
open class DefaultSshEventListener : SessionListener {

	companion object {
		val logger = getLogger(DefaultSshEventListener::class)
	}

	override fun sessionException(session: Session, exc: Throwable) {
		logger.warn("session exception", exc)
	}

	override fun sessionEvent(session: Session, event: SessionListener.Event) {
		logger.debug("session event: {}", event)
	}

	override fun sessionClosed(session: Session) {
		logger.debug("session closed")
	}

	override fun sessionCreated(session: Session) {
		logger.debug("session created")
	}
}