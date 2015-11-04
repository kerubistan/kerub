package com.github.K0zka.kerub.exceptions.mappers

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper

public class JsonMappingExceptionMapper(mapper: ObjectMapper) : AbstractExceptionMapper<JsonMappingException>(mapper) {
	companion object {
		val error = RestError("MAP1", "Mapping error.")
	}
	override fun getRestError(exception: JsonMappingException) = error
}