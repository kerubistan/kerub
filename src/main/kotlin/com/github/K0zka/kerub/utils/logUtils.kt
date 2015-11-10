package com.github.K0zka.kerub.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

fun getLogger(cl : KClass<*>) : Logger {
	return LoggerFactory.getLogger(cl.java)!!
}