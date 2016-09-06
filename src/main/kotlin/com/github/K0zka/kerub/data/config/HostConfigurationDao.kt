package com.github.K0zka.kerub.data.config

import com.github.K0zka.kerub.data.CrudDao
import com.github.K0zka.kerub.model.config.HostConfiguration
import java.util.UUID

interface HostConfigurationDao :
		CrudDao<HostConfiguration, UUID>
