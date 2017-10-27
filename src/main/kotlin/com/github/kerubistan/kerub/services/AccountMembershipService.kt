package com.github.kerubistan.kerub.services

import com.github.kerubistan.kerub.model.AccountMembership
import org.apache.shiro.authz.annotation.RequiresAuthentication
import java.util.UUID
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/account-membership/")
@RequiresAuthentication
interface AccountMembershipService {

	@PUT
	@Path("{accountId}/{membershipId}")
	fun add(
			@PathParam("accountId") accountId: UUID,
			@PathParam("membershipId") membershipId: UUID,
			accountMembership: AccountMembership
	)

	@DELETE
	@Path("{accountId}/{membershipId}")
	fun remove(@PathParam("accountId") accountId: UUID, @PathParam("membershipId") membershipId: UUID)

	@GET
	@Path("/{accountId}/members")
	fun list(@PathParam("accountId") accountId: UUID): List<AccountMembership>

	@GET
	@Path("/of/{userName}")
	fun listUserAccounts(@PathParam("userName") userName: String): List<AccountMembership>
}