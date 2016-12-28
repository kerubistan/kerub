package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.VirtualNetwork
import com.wordnik.swagger.annotations.Api
import org.apache.shiro.authz.annotation.RequiresAuthentication
import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Api("s/r/vnet", description = "Virtual network operations")
@Path("/vnet")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiresAuthentication
interface VirtualNetworkService : RestCrud<VirtualNetwork>, RestOperations.List<VirtualNetwork>