package com.github.kerubistan.kerub.services

import com.github.kerubistan.kerub.model.Pool
import org.apache.shiro.authz.annotation.RequiresAuthentication
import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/pool")
@RequiresAuthentication
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
interface PoolService : AssetService<Pool>, RestCrud<Pool>, RestOperations.List<Pool>