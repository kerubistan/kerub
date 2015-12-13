package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import javax.ws.rs.Path

@Path("/virtual-storage-dyn")
interface VirtualStorageDeviceDynamicService : DynamicService<VirtualStorageDeviceDynamic>