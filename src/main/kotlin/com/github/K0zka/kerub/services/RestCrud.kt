package com.github.K0zka.kerub.services

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.POST
import javax.ws.rs.DELETE
import javax.ws.rs.PUT
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiResponse
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiParam
import com.wordnik.swagger.annotations.ApiResponses
import java.util.UUID
import javax.ws.rs.core.MediaType
import org.apache.shiro.authz.annotation.RequiresAuthentication

Produces("application/json")
Consumes("application/json")
RequiresAuthentication
public trait RestCrud<T> : RestOperations.Read<T>, RestOperations.Add<T>, RestOperations.Update<T>, RestOperations.Delete {

}
