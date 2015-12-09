package com.github.K0zka.kerub.utils

import org.apache.sshd.common.Session
import org.apache.sshd.common.SessionListener

/**
 * Blank implementation of sshd session listener
 */
open class DefaultSshEventListener : SessionListener {
	override fun sessionEvent(session: Session, event: SessionListener.Event) {
	}

	override fun sessionClosed(session: Session) {
	}

	override fun sessionCreated(session: Session) {
	}
}