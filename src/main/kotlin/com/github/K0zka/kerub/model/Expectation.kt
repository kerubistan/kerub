package com.github.K0zka.kerub.model

import java.util.UUID
import java.io.Serializable

data trait Expectation : Serializable {
	val id : UUID
	val level : ExpectationLevel
}