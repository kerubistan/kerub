package com.github.K0zka.kerub.services.impl

import javax.ws.rs.NotFoundException

/**
 * Utility function to throw Exception if object does not exist
 * and return safe non-null reference to object if it does
 * @param entityType    the type name of entity
 * @param value the entity object
 * @param id    the ID of the entity
 */
fun <T> assertExist(entityType : String, value: T?, id: Any): T {
	if (value == null) {
		throw NotFoundException("${entityType} ${id} not found")
	} else {
		return value
	}
}