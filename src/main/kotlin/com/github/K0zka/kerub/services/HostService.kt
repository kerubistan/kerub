package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.HostPubKey
import com.github.K0zka.kerub.security.Roles
import com.github.K0zka.kerub.security.admin
import com.wordnik.swagger.annotations.*
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresRoles
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Api("s/r/host", description = "Host service")
@Path("/host")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiresRoles(admin)
@RequiresAuthentication
public interface HostService : RestCrud<Host>, RestOperations.List<Host> {

    @RequiresAuthentication
    @RequiresRoles(admin)
	override fun getById(id: UUID): Host;

    @RequiresAuthentication
    @RequiresRoles(admin)
	override fun delete(id: UUID);

    @RequiresAuthentication
    @RequiresRoles(admin)
	override fun update(id: UUID, entity: Host): Host

    @ApiOperation("Add new object")
    @ApiResponses(
            ApiResponse(code = 200, message = "OK"),
			ApiResponse(code = 403, message = "Security error")
	            )
	@PUT
    @Path("/join")
    @RequiresRoles(admin)
    @RequiresAuthentication
	fun join(@ApiParam(value = "New host with password", required = true) hostPwd : HostAndPassword): Host

    @PUT
    @Path("/join-pubkey")
    @RequiresRoles(admin)
    @RequiresAuthentication
	fun joinWithoutPassword(@ApiParam(value = "New host", required = true) host : Host): Host

    @ApiOperation("Get the public key of the server", httpMethod = "GET")
    @GET
    @Path("/helpers/pubkey")
    @RequiresRoles(admin)
    @RequiresAuthentication
	fun getHostPubkey(@QueryParam("address") address : String) : HostPubKey

    @ApiOperation("Get the public key of kerub", httpMethod = "GET", produces = MediaType.TEXT_PLAIN)
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/helpers/controller-pubkey")
    @RequiresRoles(admin)
    @RequiresAuthentication
	fun getPubkey() : String
}
