package com.github.kerubistan.kerub.utils.junix.iostat

data class IOStatEvent(
		val diskDevice : String,
		val tps : Float,
		val readKbPerSec : Float,
		val writeKbPerSec : Float,
		val readKb : Int,
		val writeKb : Int
)