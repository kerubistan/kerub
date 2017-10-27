package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ControllerConfigDaoImplTest : AbstractIspnDaoTest<String, ControllerConfig>() {

	@Test
	fun set() {
		val dao = ControllerConfigDaoImpl(cache!!, auditManager, eventListener)
		val newCfg = ControllerConfig(accountsRequired = true, ksmEnabled = false)
		val oldCfg = dao.get()
		dao.set(newCfg)
		verify(eventListener).send(any())
		verify(auditManager).auditUpdate(old = eq(oldCfg), new = eq(newCfg))
	}

	@Test
	fun get() {
		val dao = ControllerConfigDaoImpl(cache!!, auditManager, eventListener)
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