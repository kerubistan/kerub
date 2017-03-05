package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.Project
import com.github.K0zka.kerub.security.admin
import com.wordnik.swagger.annotations.Api
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresRoles
import javax.ws.rs.Path

@Api("s/r/projects", description = "Project service")
@RequiresAuthentication
@RequiresRoles(admin)
@Path("/projects") interface ProjectService : RestCrud<Project>