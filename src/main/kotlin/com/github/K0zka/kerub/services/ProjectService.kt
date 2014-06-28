package com.github.K0zka.kerub.services

import java.util.UUID
import com.github.K0zka.kerub.model.Project
import javax.ws.rs.Path

Path("/host")
public trait ProjectService : RestCrud<Project, UUID> {
}