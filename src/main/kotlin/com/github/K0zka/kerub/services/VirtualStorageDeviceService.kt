package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.VirtualStorageDevice

public interface VirtualStorageDeviceService : RestCrud<VirtualStorageDevice>, RestOperations.List<VirtualStorageDevice>