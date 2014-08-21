package com.github.K0zka.kerub.services

import java.util.UUID
import com.github.K0zka.kerub.model.Project
import javax.ws.rs.Path
import com.wordnik.swagger.annotations.Api

Api("s/r/project", description="Project service")
Path("/project")
public trait ProjectService : RestCrud<Project, UUID> {
}