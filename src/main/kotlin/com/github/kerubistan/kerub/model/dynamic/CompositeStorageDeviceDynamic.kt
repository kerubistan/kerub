package com.github.kerubistan.kerub.model.dynamic

interface CompositeStorageDeviceDynamic<out T : CompositeStorageDeviceDynamicItem>: StorageDeviceDynamic {
	val items: List<T>
}