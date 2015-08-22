package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.model.VirtualStorageDevice

public class VirtualStorageDeviceServiceImpl(dao: VirtualStorageDeviceDao) : ListableBaseService<VirtualStorageDevice>(dao, "virtual disk")