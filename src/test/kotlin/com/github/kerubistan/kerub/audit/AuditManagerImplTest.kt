package com.github.kerubistan.kerub.audit

import com.github.kerubistan.kerub.data.AuditEntryDao
import com.github.kerubistan.kerub.model.AddEntry
import com.github.kerubistan.kerub.model.DeleteEntry
import com.github.kerubistan.kerub.model.UpdateEntry
import com.github.kerubistan.kerub.testVm
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

class AuditManagerImplTest {

	val auditDao: AuditEntryDao = mock()

	@Test
	fun testAuditUpdate() {
		val auditManager = spy(AuditManagerImpl(auditDao))
		doReturn("TEST-USER").whenever(auditManager).getCurrentUser()
		auditManager.auditUpdate(testVm, testVm.copy(name = "modified"))

		verify(auditDao).add(any<UpdateEntry>())
	}

	@Test
	fun testAuditDelete() {
		val auditManager = spy(AuditManagerImpl(auditDao))
		doReturn("TEST-USER").whenever(auditManager).getCurrentUser()
		auditManager.auditDelete(testVm)

		verify(auditDao).add(any<DeleteEntry>())

	}

	@Test
	fun testAuditAdd() {
		val auditManager = spy(AuditManagerImpl(auditDao))
		doReturn("TEST-USER").whenever(auditManager).getCurrentUser()
		auditManager.auditAdd(testVm)

		verify(auditDao).add(any<AddEntry>())

	}
}