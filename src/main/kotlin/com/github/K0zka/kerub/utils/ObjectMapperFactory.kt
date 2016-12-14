package com.github.K0zka.kerub.utils

/**
 * The only reason for this factory is that spring is not able to pass arguments to the createObjectMapper method.
 * Should be removed together with spring.
 */
class ObjectMapperFactory(var prettyPrint: Boolean = true) {
	fun create() = createObjectMapper(prettyPrint)
}