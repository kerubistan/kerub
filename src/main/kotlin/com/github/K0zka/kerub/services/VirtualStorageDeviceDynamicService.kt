package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import org.apache.shiro.authz.annotation.RequiresAuthentication
import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/virtual-storage-dyn")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiresAuthentication
interface VirtualStorageDeviceDynamicService : DynamicService<VirtualStorageDeviceDynamic>