package com.github.K0zka.kerub.data

import java.util.UUID

public interface ControllerDao {
	fun get(id : String) : String?
	fun list() : List<String>
}