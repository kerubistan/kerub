package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.ControllerDao
import com.github.kerubistan.kerub.services.ControllerService

class ControllerServiceImpl(val dao: ControllerDao) : ControllerService {
	override fun list(): List<String> = dao.list()
}
