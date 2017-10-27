package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.services.VirtualStorageDeviceDynamicService

class VirtualStorageDeviceDynamicServiceImpl(dao: VirtualStorageDeviceDynamicDao)
	: AbstractDynamicServiceImpl<VirtualStorageDeviceDynamic>(dao, "vstorage-dynamic"),
		VirtualStorageDeviceDynamicService