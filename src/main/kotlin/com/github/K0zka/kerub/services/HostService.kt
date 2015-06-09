package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.Host
import java.util.UUID
import javax.ws.rs.Path
import javax.ws.rs.GET
import java.security.PublicKey
import javax.ws.rs.QueryParam
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import com.github.K0zka.kerub.host.HostCapabilitiesDiscoverer
import com.wordnik.swagger.annotations.ApiResponses
import com.wordnik.swagger.annotations.ApiResponse
import javax.ws.rs.PUT
import com.wordnik.swagger.annotations.ApiParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.Consumes
import org.apache.shiro.authz.annotation.RequiresRoles
import com.github.K0zka.kerub.security.Roles
import org.apache.shiro.authz.annotation.RequiresAuthentication

Api("s/r/host", description = "Host service")
Path("/host")
Produces(MediaType.APPLICATION_JSON)
Consumes(MediaType.APPLICATION_JSON)
RequiresRoles(Roles.admin)
RequiresAuthentication
public trait HostService : RestCrud<Host> {

	RequiresAuthentication
	RequiresRoles(Roles.admin)
	override fun getById(id: UUID): Host;

	RequiresAuthentication
	RequiresRoles(Roles.admin)
	override fun delete(id: UUID);

	RequiresAuthentication
	RequiresRoles(Roles.admin)
	override fun update(id: UUID, entity: Host): Host

	ApiOperation("Add new object")
	ApiResponses(
			ApiResponse(code = 200, message = "OK"),
			ApiResponse(code = 403, message = "Security error")
	            )
	PUT
	Path("/join")
	RequiresRoles(Roles.admin)
	RequiresAuthentication
	fun join(ApiParam(value = "New host with password", required = true) hostPwd : HostAndPassword): Host

	ApiOperation("Get the public key of the server", httpMethod = "GET")
	GET
	Path("/helpers/pubkey")
	RequiresRoles(Roles.admin)
	RequiresAuthentication
	fun getHostPubkey(QueryParam("address") address : String) : HostPubKey
}
