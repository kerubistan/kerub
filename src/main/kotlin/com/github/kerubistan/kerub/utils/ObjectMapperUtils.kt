package com.github.kerubistan.kerub.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.KotlinModule

fun createObjectMapper(prettyPrint: Boolean = true): ObjectMapper =
		ObjectMapper().registerModule(KotlinModule()).registerModule(AfterburnerModule())
				.configure(SerializationFeature.INDENT_OUTPUT, prettyPrint)
