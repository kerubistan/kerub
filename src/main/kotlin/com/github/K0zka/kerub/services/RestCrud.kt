package com.github.K0zka.kerub.services

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.POST
import javax.ws.rs.DELETE
import javax.ws.rs.PUT
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiResponse
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiParam
import com.wordnik.swagger.annotations.ApiResponses
import java.util.UUID

Produces("application/json")
Consumes("application/json")
public trait RestCrud<T> {

	ApiOperation("Get the object by it's ID.")
	ApiResponses(
			ApiResponse(code = 200, message = "OK"),
			ApiResponse(code = 403, message = "Security error"),
			ApiResponse(code = 404, message = "Not found")
	            )
	GET
	Path("/{id}")
	fun getById(ApiParam(value="ID of the object", name="id", required = true) PathParam("id") id : UUID) : T

	ApiOperation("Update the object")
	ApiResponses(
			ApiResponse(code = 200, message = "OK"),
			ApiResponse(code = 403, message = "Security error"),
			ApiResponse(code = 404, message = "Not found")
	            )
	POST
	Path("/{id}")
	fun update(ApiParam(value="ID of the object", name="id", required = true) PathParam("id") id : UUID, entity: T) : T

	ApiOperation("Delete the object")
	ApiResponses(
			ApiResponse(code = 200, message = "OK"),
			ApiResponse(code = 403, message = "Security error"),
			ApiResponse(code = 404, message = "Not found")
	            )
	DELETE
	Path("/{id}")
	fun delete(ApiParam(value="Object ID", name="id", required = true) PathParam("id") id : UUID)

	ApiOperation("Add new object")
	ApiResponses(
			ApiResponse(code = 200, message = "OK"),
			ApiResponse(code = 403, message = "Security error")
	            )
	PUT
	Path("/")
	fun add(ApiParam(value="New object", required = true) entity: T) : T
}