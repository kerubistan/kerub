package com.github.K0zka.kerub.data

public interface ControllerDao {
	fun get(id: String): String?
	fun list(): List<String>
}