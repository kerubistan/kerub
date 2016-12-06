package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.ControllerConfigDao
import com.github.K0zka.kerub.model.ControllerConfig
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ControllerConfigDaoImplTest : AbstractIspnDaoTest<String, ControllerConfig>() {

	@Test
	fun get() {
		val dao = ControllerConfigDaoImpl(cache!!, auditManager);
		assertNotNull(dao.get(), "default value expected")
		val newConfig = ControllerConfig(
				accountsRequired = true
		)
		dao.set(
				newConfig
		)
		assertEquals(newConfig, dao.get())
	}

}