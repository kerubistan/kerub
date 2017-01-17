package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.security.admin
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresRoles
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

class StatisticsInfo(
		val upTime: Long,
		val nrEntries: Long,
		val hits: Long,
		val misses: Long,
		val avgReadTime: Long,
		val avgWriteTime: Long,
		val avgRemoveTime: Long)

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/stats/controller/db")
@RequiresAuthentication
@RequiresRoles(admin)
interface StatisticsService {

	@GET
	@Path("/")
	fun listCaches() : List<String>

	@GET
	@Path("/cache/{name}")
	fun getStatisticsInfo(@PathParam("name") cacheName: String): StatisticsInfo

}