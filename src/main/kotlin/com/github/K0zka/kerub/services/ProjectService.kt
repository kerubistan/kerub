package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.Project
import com.wordnik.swagger.annotations.Api
import javax.ws.rs.Path

Api("s/r/project", description="Project service")
Path("/project")
public interface ProjectService : RestCrud<Project> {
}