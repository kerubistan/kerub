package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.wordnik.swagger.annotations.Api
import javax.ws.rs.Path

@Api("s/r/virtual-storage", description = "Virtual storage operations")
@Path("/virtual-storage")
public interface VirtualStorageDeviceService : RestCrud<VirtualStorageDevice>, RestOperations.List<VirtualStorageDevice>