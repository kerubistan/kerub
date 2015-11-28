package com.github.K0zka.kerub.utils

fun <T> List<T>.skip() : List<T> =
		if(this.isEmpty()) {
			listOf<T>()
		} else {
			this.subList(1, this.size)
		}