package com.github.kerubistan.kerub.model

interface VolumeManagerStorageCapability : StorageCapability {
	val storageDevices: List<String>
}