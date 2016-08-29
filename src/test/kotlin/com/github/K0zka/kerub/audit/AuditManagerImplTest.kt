package com.github.K0zka.kerub.audit

import com.github.K0zka.kerub.data.AuditEntryDao
import com.github.K0zka.kerub.model.AddEntry
import com.github.K0zka.kerub.model.DeleteEntry
import com.github.K0zka.kerub.model.UpdateEntry
import com.github.K0zka.kerub.testVm
import com.github.K0zka.kerub.verify
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

import org.junit.Assert.*
import org.mockito.Mockito

class AuditManagerImplTest {

	val auditDao: AuditEntryDao = mock()

	@Test
	fun testAuditUpdate() {
		val auditManager = spy(AuditManagerImpl(auditDao))
		doReturn("TEST-USER").whenever(auditManager).getCurrentUser()
		auditManager.auditUpdate(testVm, testVm.copy(name = "modified"))

		verify(auditDao).add(Mockito.any(UpdateEntry::class.java) ?: UpdateEntry(old = testVm, new = testVm, user = ""))
	}

	@Test
	fun testAuditDelete() {
		val auditManager = spy(AuditManagerImpl(auditDao))
		doReturn("TEST-USER").whenever(auditManager).getCurrentUser()
		auditManager.auditDelete(testVm)

		verify(auditDao).add(Mockito.any(UpdateEntry::class.java) ?: UpdateEntry(old = testVm, user = "", new = testVm))

	}

	@Test
	fun testAuditAdd() {
		val auditManager = spy(AuditManagerImpl(auditDao))
		doReturn("TEST-USER").whenever(auditManager).getCurrentUser()
		auditManager.auditAdd(testVm)

		verify(auditDao).add(Mockito.any(AddEntry::class.java) ?: AddEntry(new = testVm, user = ""))

	}
}