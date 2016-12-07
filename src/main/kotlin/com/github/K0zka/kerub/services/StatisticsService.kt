package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.security.admin
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresRoles
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

class StatisticsInfo(
		val upTime: Long,
		val nrEntries: Long,
		val hits: Long,
		val misses: Long,
		val avgReadTime: Long,
		val avgWriteTime: Long,
		val avgRemoveTime: Long) {
}

@Produces("application/json")
@Consumes("application/json")
@Path("/stats/controller/db")
@RequiresAuthentication interface StatisticsService {

	@RequiresRoles(admin)
	@GET
	@Path("/")
	fun listCaches() : List<String>


	@RequiresRoles(admin)
	@GET
	@Path("/{name}")
	fun getStatisticsInfo(@PathParam("name") cacheName: String): StatisticsInfo
}