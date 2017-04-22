package com.github.K0zka.kerub.model.controller.config

import com.github.K0zka.kerub.model.FsStorageCapability
import com.github.K0zka.kerub.model.GvinumStorageCapability
import com.github.K0zka.kerub.model.LvmStorageCapability
import com.github.K0zka.kerub.model.StorageCapability
import java.io.Serializable

data class StorageTechnologiesConfig(
		/**
		 * List of paths the controllers can use as file storage.
		 */
		val fsPathEnabled: List<String> = listOf("/kerub", "/storage"),
		/**
		 * List of filesystem types where the controller is allowed to allocate storage
		 */
		val fsTypeEnabled: List<String> = listOf("ext4", "ext3", "zfs", "btrfs"),
		/**
		 * The controllers can create logical volumes on vgs.
		 */
		val lvmCreateVolumeEnabled: Boolean = true,
		/**
		 * The controllers can create gvinum volumes.
		 */
		val gvinumCreateVolumeEnabled: Boolean = true,
		/**
		 * The controllers can use ceph
		 */
		val cephEnabled: Boolean = false,
		/**
		 * The controllers can use gluster
		 */
		val glusterEnabled: Boolean = false

) : Serializable {
	fun enabledCapabilities(allCapabilities: List<StorageCapability>) =
			allCapabilities.filter {
				when (it) {
					is GvinumStorageCapability ->
						gvinumCreateVolumeEnabled
					is LvmStorageCapability ->
						lvmCreateVolumeEnabled
					is FsStorageCapability ->
						it.mountPoint in fsPathEnabled && it.fsType in fsTypeEnabled
					else ->
						false
				}
			}

}