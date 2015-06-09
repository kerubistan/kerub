package com.github.K0zka.kerub.services

import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import org.apache.shiro.authz.annotation.RequiresAuthentication

public class StatisticsInfo(
		val upTime: Long,
		val nrEntries: Long,
		val hits: Long,
		val misses: Long,
		val avgReadTime: Long,
		val avgWriteTime: Long,
		val avgRemoveTime : Long) {
}


Produces("application/json")
Consumes("application/json")
Path("/stats/controller/db")
RequiresAuthentication
public interface StatisticsService {
	GET
	Path("/{name}")
	fun getStatisticsInfo(PathParam("name") cacheName : String): StatisticsInfo
}