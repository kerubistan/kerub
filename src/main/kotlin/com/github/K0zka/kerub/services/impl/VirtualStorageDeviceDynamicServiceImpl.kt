package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.services.VirtualStorageDeviceDynamicService

class VirtualStorageDeviceDynamicServiceImpl(dao: VirtualStorageDeviceDynamicDao)
	: AbstractDynamicServiceImpl<VirtualStorageDeviceDynamic>(dao, "vstorage-dynamic"),
		VirtualStorageDeviceDynamicService