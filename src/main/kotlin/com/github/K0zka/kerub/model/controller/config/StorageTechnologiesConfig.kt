package com.github.K0zka.kerub.model.controller.config

import java.io.Serializable

data class StorageTechnologiesConfig (
		/**
		 * List of paths the controllers can use as file storage.
		 */
		val fsPathEnabled : List<String> = listOf(),
		/**
		 * The controllers can create logical volumes on vgs.
		 */
		val lvmCreateVolumeEnabled : Boolean = true,
		/**
		 * The controllers can create gvinum volumes.
		 */
		val gvinumCreateVolumeEnabled : Boolean = true,
		/**
		 * The controllers can use ceph
		 */
		val cephEnabled : Boolean = false,
		/**
		 * The controllers can use gluster
		 */
		val glusterEnabled : Boolean = false

) : Serializable