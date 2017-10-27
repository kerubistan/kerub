package com.github.kerubistan.kerub.services

import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.wordnik.swagger.annotations.Api
import org.apache.cxf.jaxrs.ext.multipart.Multipart
import org.apache.shiro.authz.annotation.RequiresAuthentication
import java.io.InputStream
import java.util.UUID
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.MediaType

@RequiresAuthentication
@Api("s/r/virtual-storage", description = "Virtual storage operations")
@Path("/virtual-storage") interface VirtualStorageDeviceService :
		RestCrud<VirtualStorageDevice>,
		RestOperations.List<VirtualStorageDevice>,
		RestOperations.SimpleSearch<VirtualStorageDevice>,
		AssetService<VirtualStorageDevice> {

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/load/{id}")
	fun load(
			@PathParam("id") id: UUID,
			@Suspended async: AsyncResponse,
			@Multipart(value = "file", required = true) data: InputStream
	)

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/load/{type}/{id}")
	fun load(
			@PathParam("id") id: UUID,
			@PathParam("type") type: VirtualDiskFormat,
			@Suspended async: AsyncResponse,
			@Multipart(value = "file", required = true) data: InputStream
	)

	@GET
	@Path("/download/{type}/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	fun download(
			@PathParam("id") id: UUID,
			@PathParam("type") type: VirtualDiskFormat,
			@Suspended async: AsyncResponse
	)

}