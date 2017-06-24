package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.controller.config.ControllerConfig
import com.github.K0zka.kerub.security.admin
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresRoles
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/config")
@RequiresAuthentication
interface ControllerConfigService {
	@GET
	fun get(): ControllerConfig

	@PUT
	@RequiresRoles(admin)
	fun set(config: ControllerConfig): ControllerConfig
}