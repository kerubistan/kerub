package com.github.kerubistan.kerub.services

import com.github.kerubistan.kerub.model.Project
import com.github.kerubistan.kerub.security.admin
import com.wordnik.swagger.annotations.Api
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresRoles
import javax.ws.rs.Path

@Api("s/r/projects", description = "Project service")
@RequiresAuthentication
@RequiresRoles(admin)
@Path("/projects")
interface ProjectService : RestCrud<Project>