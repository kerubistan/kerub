package com.github.kerubistan.kerub.model.controller.config

import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.GvinumStorageCapability
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.StorageCapability
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
		val glusterEnabled: Boolean = false,
		/**
		 * The controllers can use NFS
		 */
		val nfsEnabled: Boolean = true,
		/**
		 * The controllers can use ISCSI
		 */
		val iscsiEnabled: Boolean = true,
		/**
		 * Should the controller run benchmarks on the storage
		 */
		val storagebenchmarkingEnbled: Boolean = true
) : Serializable {

	fun isEnabled(storageCapability: StorageCapability) =
			when (storageCapability) {
				is GvinumStorageCapability ->
					gvinumCreateVolumeEnabled
				is LvmStorageCapability ->
					lvmCreateVolumeEnabled
				is FsStorageCapability ->
					storageCapability.mountPoint in fsPathEnabled
							&& storageCapability.fsType in fsTypeEnabled
				else ->
					false
			}

	fun enabledCapabilities(allCapabilities: List<StorageCapability>) =
			allCapabilities.filter(this::isEnabled)

}