package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.exc.HostAddressException
import com.github.K0zka.kerub.utils.ipmi.IpmiClient
import nl.komponents.kovenant.then
import java.net.UnknownHostException
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.MediaType

@Path("/helpers/power/ipmi")
@Produces(MediaType.APPLICATION_JSON)
class IpmiService(private val ipmiClient: IpmiClient = IpmiClient()) {
	@GET
	@Path("/ping")
	fun ping(@Suspended async: AsyncResponse,
			 @QueryParam("address") address: String,
			 @QueryParam("timeout") timeoutMs: Int = 1000) {
		val timeOut = maxOf(timeoutMs, 2000)
		ipmiClient.sendPing(address, timeOut) then {
			async.resume(it)
		} fail {
			async.resume(
					when (it) {
						is UnknownHostException -> HostAddressException(address)
						else -> it
					}
			)
		}
	}
}