package com.github.kerubistan.kerub.services

import com.github.kerubistan.kerub.model.Account
import com.github.kerubistan.kerub.security.admin
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresRoles
import java.util.UUID
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Api(value = "Account handling operations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/accounts")
@RequiresAuthentication
interface AccountService : RestCrud<Account>, RestOperations.List<Account>, RestOperations.SimpleSearch<Account> {

	@ApiOperation(value = "Create account")
	@PUT
	@Path("/")
	@RequiresRoles(admin)
	override fun add(entity: Account): Account

	@DELETE
	@Path("/{id}")
	@RequiresRoles(admin)
	override fun delete(@PathParam("id") id: UUID)
}