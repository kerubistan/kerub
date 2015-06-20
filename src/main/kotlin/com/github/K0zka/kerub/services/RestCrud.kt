package com.github.K0zka.kerub.services

import org.apache.shiro.authz.annotation.RequiresAuthentication
import javax.ws.rs.Consumes
import javax.ws.rs.Produces

Produces("application/json")
Consumes("application/json")
RequiresAuthentication
public interface RestCrud<T> : RestOperations.Read<T>, RestOperations.Add<T>, RestOperations.Update<T>, RestOperations.Delete {

}
