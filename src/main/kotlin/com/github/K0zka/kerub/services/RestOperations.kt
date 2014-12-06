package com.github.K0zka.kerub.services

import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiResponses
import com.wordnik.swagger.annotations.ApiResponse
import javax.ws.rs.GET
import javax.ws.rs.Path
import com.wordnik.swagger.annotations.ApiParam
import javax.ws.rs.PathParam
import java.util.UUID
import javax.ws.rs.PUT
import javax.ws.rs.DELETE
import javax.ws.rs.POST
import javax.ws.rs.QueryParam
import javax.ws.rs.DefaultValue

/**
 * Collection of typical operations.
 */
trait RestOperations {
	trait Read<T> {
		ApiOperation("Get the object by it's ID.")
		ApiResponses(
				ApiResponse(code = 200, message = "OK"),
				ApiResponse(code = 403, message = "Security error"),
				ApiResponse(code = 404, message = "Not found")
		            )
		GET
		Path("/{id}")
		fun getById(ApiParam(value = "ID of the object", name = "id", required = true) PathParam("id") id: UUID): T
	}

	trait Add<T> {
		ApiOperation("Add new object")
		ApiResponses(
				ApiResponse(code = 200, message = "OK"),
				ApiResponse(code = 403, message = "Security error")
		            )
		PUT
		Path("/")
		fun add(ApiParam(value = "New object", required = true) entity: T): T

	}

	trait Delete {
		ApiOperation("Delete the object")
		ApiResponses(
				ApiResponse(code = 200, message = "OK"),
				ApiResponse(code = 403, message = "Security error"),
				ApiResponse(code = 404, message = "Not found")
		            )
		DELETE
		Path("/{id}")
		fun delete(ApiParam(value = "Object ID", name = "id", required = true) PathParam("id") id: UUID)

	}

	trait Update<T> {
		ApiOperation("Update the object")
		ApiResponses(
				ApiResponse(code = 200, message = "OK"),
				ApiResponse(code = 403, message = "Security error"),
				ApiResponse(code = 404, message = "Not found")
		            )
		POST
		Path("/{id}")
		fun update(ApiParam(value = "ID of the object", name = "id", required = true) PathParam("id") id: UUID, entity: T): T
	}

	trait List<T> {
		ApiOperation("List all objects", notes = "The actual list you get will be filtered by security")
		GET
		Path("/")
		fun listAll(ApiParam("First returned entity", defaultValue = "0", required = false)
		            QueryParam("start")
		            DefaultValue("0") start : Long,
		            ApiParam("Maximum number of returned entities", defaultValue = "20", required = false)
		            QueryParam("limit")
		            DefaultValue("20") limit : Long,
		            ApiParam("Property name to sort by", defaultValue = "id", required = false)
		            QueryParam("sort")
		            DefaultValue("id") sort : String
		           ) : ResultPage<T>
	}
}