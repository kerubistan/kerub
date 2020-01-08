package com.github.kerubistan.kerub.model.index

interface Indexed<out T : Any> {
	val index: T
}