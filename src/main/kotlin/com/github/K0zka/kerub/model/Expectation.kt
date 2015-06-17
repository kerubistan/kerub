package com.github.K0zka.kerub.model

import java.util.UUID
import java.io.Serializable

/**
 * Base expectation interface.
 * Expectations describe SLA on virtual resources.
 */
data interface Expectation: Serializable {
	val id : UUID
	val level : ExpectationLevel
}
