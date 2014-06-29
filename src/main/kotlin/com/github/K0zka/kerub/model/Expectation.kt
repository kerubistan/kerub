package com.github.K0zka.kerub.model

import java.util.UUID
import java.io.Serializable

trait Expectation : Serializable {
	var id : UUID?
}