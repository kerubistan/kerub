package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.utils.ipmi.IpmiClient
import java.lang.IllegalStateException
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended

@Path("/helpers/power/ipmi")
class IpmiService(private val ipmiClient: IpmiClient = IpmiClient()) {
	@GET
	@Path("/ping")
	fun ping(@Suspended async: AsyncResponse,
			 @QueryParam("address") address: String,
			 @QueryParam("timeout") timeoutMs: Int = 1000) {
		val timeOut = maxOf(timeoutMs, 2000)
		val start = System.currentTimeMillis()
		ipmiClient.sendPing(address, timeOut, onPong = {
			async.resume(System.currentTimeMillis() - start)
		}, onTimeout = {
			async.resume(IllegalStateException("ping timeout after $timeOut ms"))
		})
	}
}