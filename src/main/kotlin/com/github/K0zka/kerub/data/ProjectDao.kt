package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.Project
import java.util.UUID

public interface ProjectDao : ListableCrudDao<Project, UUID> {
}