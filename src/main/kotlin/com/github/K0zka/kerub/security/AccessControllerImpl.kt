package com.github.K0zka.kerub.security


import com.github.K0zka.kerub.data.AccountMembershipDao
import com.github.K0zka.kerub.data.ControllerConfigDao
import com.github.K0zka.kerub.data.ProjectmembershipDao
import com.github.K0zka.kerub.model.Asset
import com.github.K0zka.kerub.model.AssetOwnerType
import com.github.K0zka.kerub.model.VirtualMachine
import org.apache.shiro.SecurityUtils.getSubject
import java.util.UUID

class AccessControllerImpl(
		private val controllerConfigDao: ControllerConfigDao,
		private val accountMembershipDao: AccountMembershipDao,
		private val projectmembershipDao: ProjectmembershipDao,
		private val validator: Validator
) : AccessController {
	override fun <T : Asset> doAndCheck(action: () -> T?): T? {
		val result = action()

		return if (result == null) {
			null
		} else {
			return checkMembership(result)
		}
	}

	private fun <T : Asset> checkMembership(result: T): T {
		val owner = result.owner
		return if (owner == null) {
			result
		} else when (owner.ownerType) {
			AssetOwnerType.account -> doAsAccountMember(owner.ownerId) { result }
			else -> TODO()
		}
	}

	override fun <T> checkAndDo(asset: Asset, action: () -> T?): T? {
		val owner = asset.owner
		validator.validate(asset)
		val config = controllerConfigDao.get()
		return if (owner != null) {
			if (owner.ownerType == AssetOwnerType.account) {
				doAsAccountMember(owner.ownerId) {
					action()
				}
			} else {
				TODO("handle projects here")
			}
		} else {
			if (config.accountsRequired) {
				throw IllegalArgumentException("accounts required")
			} else {
				action()
			}
		}
	}

	override fun <T> doAsAccountMember(accountId: UUID, action: () -> T): T {
		if (getSubject().hasRole(admin)
				|| accountMembershipDao.isAccountMember(getSubject().principal.toString(), accountId)) {
			return action()
		} else {
			throw SecurityException("No permission")
		}
	}
}