package com.github.kerubistan.kerub.services

import org.apache.shiro.authz.annotation.RequiresAuthentication
import javax.ws.rs.Consumes
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiresAuthentication interface RestCrud<T> : RestOperations.Read<T>, RestOperations.Add<T>, RestOperations.Update<T>, RestOperations.Delete
