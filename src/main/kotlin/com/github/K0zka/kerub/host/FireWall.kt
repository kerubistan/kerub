package com.github.K0zka.kerub.host

interface FireWall {
	fun open(port: Int, proto: String)
	fun close(port: Int, proto: String)
}