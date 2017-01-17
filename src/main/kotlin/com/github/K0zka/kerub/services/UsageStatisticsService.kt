package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.data.stat.BasicBalanceReport
import com.github.K0zka.kerub.security.admin
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresRoles
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/stats/usage")
@RequiresAuthentication
@RequiresRoles(admin)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
interface UsageStatisticsService {

	@GET
	@Path("/balance")
	fun basicBalanceReport(): BasicBalanceReport

}