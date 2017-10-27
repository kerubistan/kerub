package com.github.kerubistan.kerub.data.dynamic

import com.github.kerubistan.kerub.data.DaoOperations
import com.github.kerubistan.kerub.model.dynamic.ControllerDynamic

interface ControllerDynamicDao
	: DaoOperations.Add<ControllerDynamic, String>,
		DaoOperations.Read<ControllerDynamic, String>,
		DaoOperations.SimpleList<ControllerDynamic>