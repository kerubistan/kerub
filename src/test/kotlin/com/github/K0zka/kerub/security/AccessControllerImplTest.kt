package com.github.K0zka.kerub.security

import com.github.K0zka.kerub.data.AccountMembershipDao
import com.github.K0zka.kerub.data.ControllerConfigDao
import com.github.K0zka.kerub.data.ProjectmembershipDao
import com.github.K0zka.kerub.expect
import com.github.K0zka.kerub.model.Asset
import com.github.K0zka.kerub.model.AssetOwner
import com.github.K0zka.kerub.model.AssetOwnerType
import com.github.K0zka.kerub.model.ControllerConfig
import com.github.K0zka.kerub.model.VirtualNetwork
import com.github.K0zka.kerub.testVm
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.shiro.SecurityUtils
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.ThreadContext
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AccessControllerImplTest {

	val controllerConfigDao: ControllerConfigDao = mock()
	val accountMembershipDao: AccountMembershipDao = mock()
	val projectmembershipDao: ProjectmembershipDao = mock()
	val securityManager: SecurityManager = mock()
	val subject = mock<Subject>()
	val validator = mock<Validator>()

	@Test
	fun doAndCheck() {
		val noOWnerVm = testVm.copy(
				owner = null
		)
		val myAccountId = UUID.randomUUID()
		val ownedVm = testVm.copy(
				owner = AssetOwner(
						ownerId = myAccountId,
						ownerType = AssetOwnerType.account
				)
		)
		val otherAccountId = UUID.randomUUID()
		val someoneElsesVm = testVm.copy(
				owner = AssetOwner(
						ownerId = otherAccountId,
						ownerType = AssetOwnerType.account
				)
		)

		whenever(accountMembershipDao.isAccountMember(eq("test-user"), eq(myAccountId))).thenReturn(true)
		whenever(accountMembershipDao.isAccountMember(eq("test-user"), eq(otherAccountId))).thenReturn(true)

		assertEquals(noOWnerVm, AccessControllerImpl(controllerConfigDao, accountMembershipDao, projectmembershipDao, validator).doAndCheck {
			noOWnerVm
		})
		assertEquals(ownedVm, AccessControllerImpl(controllerConfigDao, accountMembershipDao, projectmembershipDao, validator).doAndCheck {
			ownedVm
		})
		assertNull(AccessControllerImpl(controllerConfigDao, accountMembershipDao, projectmembershipDao, validator).doAndCheck {
			null
		})
	}

	@Test
	fun doAsAccountMemberAsAdmin() {
		whenever(subject.hasRole(admin)).thenReturn(true)
		val action = mock<() -> Asset>()
		val accountId = UUID.randomUUID()
		AccessControllerImpl(controllerConfigDao, accountMembershipDao, projectmembershipDao, validator).doAsAccountMember(
				accountId = accountId,
				action = action
		)

		verify(action).invoke()
	}

	@Test
	fun doAsAccountMemberAsAccountMember() {
		val action = mock<() -> Asset>()
		val accountId = UUID.randomUUID()
		whenever(subject.hasRole(admin)).thenReturn(false)
		whenever(accountMembershipDao.isAccountMember(eq("test-user"), eq(accountId))).thenReturn(true)
		AccessControllerImpl(controllerConfigDao, accountMembershipDao, projectmembershipDao, validator).doAsAccountMember(
				accountId = accountId,
				action = action
		)

		verify(action).invoke()
	}

	@Test
	fun doAsAccountMemberAsNotMember() {
		val action = mock<() -> Asset>()
		val accountId = UUID.randomUUID()
		whenever(subject.hasRole(admin)).thenReturn(false)
		whenever(accountMembershipDao.isAccountMember(eq("test-user"), eq(accountId))).thenReturn(false)
		expect(SecurityException::class) {
			AccessControllerImpl(controllerConfigDao, accountMembershipDao, projectmembershipDao, validator).doAsAccountMember(
					accountId = accountId,
					action = action
			)
		}
		verify(action, never()).invoke()
	}

	@Before
	fun setup() {
		SecurityUtils.setSecurityManager(securityManager)
		whenever(securityManager.createSubject(any())).thenReturn(subject)
		whenever(subject.principal).thenReturn("test-user")
	}

	@After
	fun cleanup() {
		//otherwise the subjects from the previous test runs remain in the shiro threadlocal
		ThreadContext.remove()
	}

	@Test
	fun checkAndDoWithoutRequiredAccount() {
		setAccountRequired(true)
		val callback = mock<() -> Asset>()
		expect(IllegalArgumentException::class) {
			AccessControllerImpl(controllerConfigDao, accountMembershipDao, projectmembershipDao, validator).checkAndDo(
					VirtualNetwork(
							id = UUID.randomUUID(),
							name = "blah",
							owner = null
					), callback
			)
		}
		verify(callback, never()).invoke()
	}

	@Test
	fun checkAndDoWithRequiredAccountNotmember() {
		setAccountRequired(true)
		val callback = mock<() -> Asset>()
		val ownerId = UUID.randomUUID()
		whenever(accountMembershipDao.isAccountMember(eq("test-user"), eq(ownerId))).thenReturn(false)

		expect(SecurityException::class) {
			AccessControllerImpl(controllerConfigDao, accountMembershipDao, projectmembershipDao, validator).checkAndDo(
					VirtualNetwork(
							id = UUID.randomUUID(),
							name = "blah",
							owner = AssetOwner(
									ownerId = ownerId,
									ownerType = AssetOwnerType.account
							)
					), callback
			)
		}
		verify(callback, never()).invoke()
	}

	@Test
	fun checkAndDoWithRequiredAccountYesMember() {
		setAccountRequired(true)
		val callback = mock<() -> Asset>()
		val ownerId = UUID.randomUUID()
		whenever(accountMembershipDao.isAccountMember(eq("test-user"), eq(ownerId))).thenReturn(true)

		AccessControllerImpl(controllerConfigDao, accountMembershipDao, projectmembershipDao, validator).checkAndDo(
				VirtualNetwork(
						id = UUID.randomUUID(),
						name = "blah",
						owner = AssetOwner(
								ownerId = ownerId,
								ownerType = AssetOwnerType.account
						)
				), callback
		)
		verify(callback).invoke()
	}

	private fun setAccountRequired(accountsRequired: Boolean) {
		whenever(controllerConfigDao.get()).thenReturn(ControllerConfig(
				accountsRequired = accountsRequired
		))
	}

	@Test
	fun checkAndDoWithOutNonnrequiredAccount() {
		setAccountRequired(false)
		val callback = mock<() -> Asset>()
		AccessControllerImpl(controllerConfigDao, accountMembershipDao, projectmembershipDao, validator).checkAndDo(
				VirtualNetwork(
						id = UUID.randomUUID(),
						name = "blah",
						owner = null
				), callback
		)
		verify(callback).invoke()
	}

}