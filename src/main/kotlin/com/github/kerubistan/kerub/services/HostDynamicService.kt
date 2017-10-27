package com.github.kerubistan.kerub.services

import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.security.admin
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresRoles
import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiresRoles(admin)
@RequiresAuthentication
@Path("host-dyn")
interface HostDynamicService : DynamicService<HostDynamic>