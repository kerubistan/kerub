package com.github.kerubistan.kerub.host

interface FireWall {
	fun open(port: Int, proto: String)
	fun close(port: Int, proto: String)
}