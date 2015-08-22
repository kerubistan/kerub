package com.github.K0zka.kerub.services

import org.apache.shiro.authz.annotation.RequiresAuthentication
import javax.ws.rs.*

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