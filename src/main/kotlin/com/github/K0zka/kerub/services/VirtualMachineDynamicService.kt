package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import javax.ws.rs.Path

@Path("/vm-dyn")
interface VirtualMachineDynamicService : DynamicService<VirtualMachineDynamic>