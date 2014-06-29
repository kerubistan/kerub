package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.services.HostService
import java.util.UUID
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.ListableDao

public class HostServiceImpl(dao : HostDao) : BaseServiceImpl<Host, UUID>(dao, "host"), HostService {
	override fun listAll(): List<Host> {
		return (dao as ListableDao<Host, UUID>).listAll()
	}
}