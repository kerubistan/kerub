package com.github.K0zka.kerub.security

import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.data.VirtualNetworkDao
import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.model.AssetOwner
import com.github.K0zka.kerub.model.AssetOwnerType
import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.VirtualNetwork
import com.github.K0zka.kerub.model.VirtualStorageLink
import com.github.K0zka.kerub.model.expectations.NotSameHostExpectation
import com.github.K0zka.kerub.model.io.BusType
import com.github.K0zka.kerub.model.io.DeviceType
import com.github.K0zka.kerub.testDisk
import com.github.K0zka.kerub.testVm
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertFalse
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class ValidatorImplTest {

	val vmDao = mock<VirtualMachineDao>()
	val vnetDao = mock<VirtualNetworkDao>()
	val vdiskDao = mock<VirtualStorageDeviceDao>()

	val account1 = UUID.randomUUID()
	val account2 = UUID.randomUUID()


	@Test
	fun validate() {
		val linkedDisk = testDisk.copy(
				id = UUID.randomUUID(),
				owner = AssetOwner(
						ownerId = account1,
						ownerType = AssetOwnerType.account
				)
		)
		whenever(vdiskDao.get(any<List<UUID>>())).thenReturn(listOf(
				linkedDisk
		))
		whenever(vnetDao.get(any<List<UUID>>())).thenReturn(
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
		whenever(vmDao.get(any<List<UUID>>())).thenReturn(
				listOf(
						otherVm
				)
		)
		ValidatorImpl(vmDao, vnetDao, vdiskDao).validate(testVm.copy(
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
		whenever(vdiskDao.get(any<List<UUID>>())).thenReturn(listOf(
				linkedDisk
		))
		whenever(vnetDao.get(any<List<UUID>>())).thenReturn(
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
		whenever(vmDao.get(any<List<UUID>>())).thenReturn(
				listOf(
						otherVm
				)
		)
		try {
			ValidatorImpl(vmDao, vnetDao, vdiskDao).validate(testVm.copy(
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
