package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.ControllerDao
import com.github.K0zka.kerub.services.ControllerService

class ControllerServiceImpl(val dao: ControllerDao) : ControllerService {
	override fun list(): List<String> {
		return dao.list()
	}

}
