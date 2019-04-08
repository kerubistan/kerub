package com.github.kerubistan.kerub.services

import com.github.kerubistan.kerub.security.admin
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresRoles
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Response

@Path("admin/support/db-dump")
@RequiresAuthentication
@RequiresRoles(admin)
interface DbDumpService {
	@GET
	@Produces("application/tar")
	fun dump(): Response

}