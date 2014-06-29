package com.github.K0zka.kerub.services.impl

import javax.ws.rs.NotFoundException

/**
 * Utility function to throw Exception if object does not exist
 * and return safe non-null reference to object if it does
 */
fun assertExist<T>(entityType : String, value: T?, id: Any): T {
	if (value == null) {
		throw NotFoundException("${entityType} ${id} not found")
	} else {
		return value
	}
}