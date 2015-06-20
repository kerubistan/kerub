package com.github.K0zka.kerub.model

import java.io.Serializable
import java.util.UUID

/**
 * Base expectation interface.
 * Expectations describe SLA on virtual resources.
 */
data interface Expectation: Serializable {
	val id : UUID
	val level : ExpectationLevel
}
