package com.github.kerubistan.kerub.services.impl

import org.junit.Test

import org.junit.Assert.*

class AccountServiceImplTest {

	@Test
	fun search() {
	}

	@Test
	fun getById() {
		AccountServiceImpl::class.java.methods.forEach { println("$it ${it.isSynthetic}") }
	}
}