package com.github.kerubistan.kerub.data

interface ControllerDao {
	fun get(id: String): String?
	fun list(): List<String>
}