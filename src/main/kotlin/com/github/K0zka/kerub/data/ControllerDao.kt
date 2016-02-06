package com.github.K0zka.kerub.data

interface ControllerDao {
	fun get(id: String): String?
	fun list(): List<String>
}