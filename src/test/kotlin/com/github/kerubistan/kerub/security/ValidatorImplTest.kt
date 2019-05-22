package com.github.kerubistan.kerub.security

import com.github.kerubistan.kerub.data.hub.AnyAssetDao
import com.github.kerubistan.kerub.model.AssetOwner
import com.github.kerubistan.kerub.model.AssetOwnerType
import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualNetwork
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.VirtualStorageLink
import com.github.kerubistan.kerub.model.expectations.NotSameHostExpectation
import com.github.kerubistan.kerub.model.io.BusType
import com.github.kerubistan.kerub.model.io.DeviceType
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testVm
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertFalse
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class ValidatorImplTest {

	private val anyAssetDao: AnyAssetDao = mock()

	private val account1 = UUID.randomUUID()
	private val account2 = UUID.randomUUID()


	@Test
	fun validate() {
		val linkedDisk = testDisk.copy(
				id = UUID.randomUUID(),
				owner = AssetOwner(
						ownerId = account1,
						ownerType = AssetOwnerType.account
				)
		)
		whenever(anyAssetDao.getAll(eq(VirtualStorageDevice::class), any<List<UUID>>()))
				.thenReturn(listOf(linkedDisk))
		whenever(anyAssetDao.getAll(eq(VirtualNetwork::class), any<List<UUID>>())).thenReturn(
				listOf(
						VirtualNetwork(
								id = UUID.randomUUID(),
								name = "vnet-1",
								owner = AssetOwner(
										ownerId = account1,
										ownerType = AssetOwnerType.account
								)
						)
				)
		)
		val otherVm = testVm.copy(
				id = UUID.randomUUID(),
				owner = AssetOwner(
						ownerId = account1,
						ownerType = AssetOwnerType.account
				)
		)
		whenever(anyAssetDao.getAll(eq(VirtualMachine::class), any<List<UUID>>())).thenReturn(
				listOf(
						otherVm
				)
		)
		ValidatorImpl(anyAssetDao).validate(testVm.copy(
				id = UUID.randomUUID(),
				owner = AssetOwner(
						ownerId = account1,
						ownerType = AssetOwnerType.account
				),
				virtualStorageLinks = listOf(
						VirtualStorageLink(
								bus = BusType.sata,
								device = DeviceType.disk,
								readOnly = false,
								virtualStorageId = linkedDisk.id
						)
				),
				expectations = listOf(
						NotSameHostExpectation(
								ExpectationLevel.Want,
								otherVmId = otherVm.id
						)
				)
		))
	}

	@Test
	fun validateAndFailWithDisk() {
		val linkedDisk = testDisk.copy(
				id = UUID.randomUUID(),
				owner = AssetOwner(
						ownerId = account2,
						ownerType = AssetOwnerType.account
				)
		)
		whenever(anyAssetDao.getAll(eq(VirtualStorageDevice::class), any<List<UUID>>()))
				.thenReturn(listOf(linkedDisk))
		whenever(anyAssetDao.getAll(eq(VirtualNetwork::class), any<List<UUID>>())).thenReturn(
				listOf(
						VirtualNetwork(
								id = UUID.randomUUID(),
								name = "vnet-1",
								owner = AssetOwner(
										ownerId = account1,
										ownerType = AssetOwnerType.account
								)
						)
				)
		)
		val otherVm = testVm.copy(
				id = UUID.randomUUID(),
				owner = AssetOwner(
						ownerId = account1,
						ownerType = AssetOwnerType.account
				)
		)
		whenever(anyAssetDao.getAll(eq(VirtualMachine::class), any<List<UUID>>()))
				.thenReturn(listOf(otherVm))
		try {
			ValidatorImpl(anyAssetDao).validate(testVm.copy(
					id = UUID.randomUUID(),
					owner = AssetOwner(
							ownerId = account1,
							ownerType = AssetOwnerType.account
					),
					virtualStorageLinks = listOf(
							VirtualStorageLink(
									bus = BusType.sata,
									device = DeviceType.disk,
									readOnly = false,
									virtualStorageId = linkedDisk.id
							)
					),
					expectations = listOf(
							NotSameHostExpectation(
									ExpectationLevel.Want,
									otherVmId = otherVm.id
							)
					)
			))
			fail("should have failed")
		} catch (ise : IllegalStateException) {
			//verify that account2 is not mentioned in the message
			assertFalse(ise.message!!.contains(account2.toString()))
		}
	}

}
