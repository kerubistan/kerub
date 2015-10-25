package com.github.K0zka.kerub.services

import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiParam
import com.wordnik.swagger.annotations.ApiResponse
import com.wordnik.swagger.annotations.ApiResponses
import org.apache.shiro.authz.annotation.RequiresAuthentication
import java.util.UUID
import javax.ws.rs.*

/**
 * Collection of typical operations.
 */
interface RestOperations {
	/**
	 * Interface for rest services that allow clients to retrieve an entity by its ID.
	 */
	interface Read<T> {
		@ApiOperation("Get the object by it's ID.")
        @ApiResponses(
				ApiResponse(code = 200, message = "OK"),
				ApiResponse(code = 403, message = "Security error"),
				ApiResponse(code = 404, message = "Not found")
		            )
        @GET
        @Path("/{id}")
        @RequiresAuthentication
		fun getById(@ApiParam(value = "ID of the object", name = "id", required = true) @PathParam("id") id: UUID): T
	}

	/**
	 * Interface for rest services that allow clients to add an entity.
	 */
	interface Add<T> {
        @ApiOperation("Add new object")
        @ApiResponses(
                ApiResponse(code = 200, message = "OK"),
                ApiResponse(code = 403, message = "Security error")
        )
        @PUT
        @Path("/")
        @RequiresAuthentication
		fun add(@ApiParam(value = "New object", required = true) entity: T): T

	}

	/**
	 * Interface for rest services that allow clients to delete an entity.
	 */
	interface Delete {
        @ApiOperation("Delete the object")
        @ApiResponses(
                ApiResponse(code = 200, message = "OK"),
                ApiResponse(code = 403, message = "Security error"),
                ApiResponse(code = 404, message = "Not found")
		            )
		@DELETE
		@Path("/{id}")
        @RequiresAuthentication
		fun delete(@ApiParam(value = "Object ID", name = "id", required = true) @PathParam("id") id: UUID)

	}

	/**
	 * Interface for rest services that allow clients to update an entity.
	 */
	interface Update<T> {
		@ApiOperation("Update the object")
        @ApiResponses(
				ApiResponse(code = 200, message = "OK"),
				ApiResponse(code = 403, message = "Security error"),
				ApiResponse(code = 404, message = "Not found")
		            )
        @POST
        @Path("/{id}")
        @RequiresAuthentication
		fun update(@ApiParam(value = "ID of the object", name = "id", required = true) @PathParam("id") id: UUID, entity: T): T
	}

	/**
	 * Interface for rest services that allow clients to list entities broken down to pages.
	 */
	interface List<T> {
        @ApiOperation("List all objects", notes = "The actual list you get will be filtered by security")
        @GET
        @Path("/")
        @RequiresAuthentication
		fun listAll(@ApiParam("First returned entity", defaultValue = "0", required = false)
                    @QueryParam("start")
                    @DefaultValue("0") start : Long,
                    @ApiParam("Maximum number of returned entities", defaultValue = "20", required = false)
                    @QueryParam("limit")
                    @DefaultValue("20") limit : Long,
                    @ApiParam("Property name to sort by", defaultValue = "id", required = false)
                    @QueryParam("sort")
                    @DefaultValue("id") sort : String
		           ) : ResultPage<T>
	}
}
