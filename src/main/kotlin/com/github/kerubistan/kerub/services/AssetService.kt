package com.github.kerubistan.kerub.services

import com.github.kerubistan.kerub.model.Asset
import com.github.kerubistan.kerub.model.AssetOwnerType
import com.github.kerubistan.kerub.model.paging.SearchResultPage
import com.github.kerubistan.kerub.model.paging.SortResultPage
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiParam
import org.apache.shiro.authz.annotation.RequiresAuthentication
import java.util.UUID
import javax.ws.rs.DefaultValue
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType

@RequiresAuthentication
interface AssetService<T : Asset> : RestOperations.ByName<T>, RestOperations.SimpleSearch<T> {
	@ApiOperation("List all objects", notes = "The actual list you get will be filtered by security")
	@GET
	@Path("/{ownerType}/ownerId/")
	fun listByOwner(@ApiParam("First returned entity", defaultValue = "0", required = false)
					@QueryParam("start")
					@DefaultValue("0") start: Long,

					@ApiParam("Maximum number of returned entities", defaultValue = "20", required = false)
					@QueryParam("limit")
					@DefaultValue("20") limit: Int,

					@ApiParam("Property name to sort by", defaultValue = "id", required = false)
					@QueryParam("sort")
					@DefaultValue("id") sort: String,

					@ApiParam("Type of the owner (account/project)", required = false)
					@QueryParam("ownerType")
					ownerType: AssetOwnerType,

					@ApiParam("ID of the owner", defaultValue = "id", required = false)
					@QueryParam("ownerType")
					ownerId: UUID

	): SortResultPage<T>

	@ApiOperation("Search objects within the account/project", notes = "The actual list will be filtered by security")
	@GET
	@Path("/search/{ownerType}/{ownerId}")
	fun search(
			@ApiParam("Name of the field to search by", defaultValue = "0", required = false)
			@QueryParam("field")
			field: String,

			@ApiParam("Value of the field to search for", defaultValue = "0", required = false)
			@QueryParam("value")
			value: String,

			@ApiParam("First returned entity", defaultValue = "0", required = false)
			@QueryParam("start")
			@DefaultValue("0") start: Long = 0,

			@ApiParam("Maximum number of returned entities", defaultValue = "20", required = false)
			@QueryParam("limit")
			@DefaultValue("20") limit: Int = 20,

			@ApiParam("Type of the owner (account/project)", required = false)
			@QueryParam("ownerType")
			ownerType: AssetOwnerType,

			@ApiParam("ID of the owner", defaultValue = "id", required = false)
			@QueryParam("ownerType")
			ownerId: UUID

	): SearchResultPage<T>

	@ApiOperation("Search objects within the account/project", notes = "The actual list will be filtered by security")
	@GET
	@Path("/byname/{ownerType}/{ownerId}/{name}")
	fun getByNameAndOwner(@PathParam("ownerType") ownerType: AssetOwnerType,
						  @PathParam("ownerId") ownerId: UUID,
						  @PathParam("name") name: String): List<T>

	@GET
	@Path("/autoname")
	@Produces(MediaType.TEXT_PLAIN)
	fun autoName(): String

	@GET
	@Path("/autoname/{ownerType}/{ownerId}")
	@Produces(MediaType.TEXT_PLAIN)
	fun autoName(ownerType: AssetOwnerType, ownerId: UUID): String

}