package com.github.kerubistan.kerub.services

import com.github.kerubistan.kerub.model.Template
import org.apache.shiro.authz.annotation.RequiresAuthentication
import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/template")
@RequiresAuthentication
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
interface TemplateService : AssetService<Template>, RestCrud<Template>
