package com.github.K0zka.kerub.data

import java.util.UUID

trait ControllerDao {
	fun get(id : String) : String?
	fun list() : List<String>
}