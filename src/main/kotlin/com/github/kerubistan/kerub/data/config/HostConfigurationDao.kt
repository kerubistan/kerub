package com.github.kerubistan.kerub.data.config

import com.github.kerubistan.kerub.data.CrudDao
import com.github.kerubistan.kerub.model.config.HostConfiguration
import java.util.UUID

interface HostConfigurationDao :
		CrudDao<HostConfiguration, UUID>