package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.services.VirtualStorageDeviceService

public class VirtualStorageDeviceServiceImpl(dao: VirtualStorageDeviceDao) : VirtualStorageDeviceService, ListableBaseService<VirtualStorageDevice>(dao, "virtual disk")