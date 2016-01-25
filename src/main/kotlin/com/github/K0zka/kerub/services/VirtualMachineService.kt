package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.VirtualMachine
import com.wordnik.swagger.annotations.Api
import org.apache.shiro.authz.annotation.RequiresAuthentication
import java.util.UUID
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Api("s/r/vm", description = "Virtual machine operations")
@Path("/vm")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiresAuthentication
public interface VirtualMachineService : RestCrud<VirtualMachine>, RestOperations.List<VirtualMachine> {
	@Path("{id}/start")
	@POST
	fun startVm(@PathParam("id") id: UUID)

	@Path("{id}/stop")
	@POST
	fun stopVm(@PathParam("id") id: UUID)

	@Path("{id}/connection/spice")
	@Produces("application/x-virt-viewer")
	@GET
	fun spiceConnection() : String {

		return """
[virt-viewer]
type=spice
host=${TODO("https://github.com/kerubistan/kerub/issues/77")}
port=${TODO("https://github.com/kerubistan/kerub/issues/77")}
password=${TODO("https://github.com/kerubistan/kerub/issues/77")}
delete-this-file=1
fullscreen=0
title=${TODO("https://github.com/kerubistan/kerub/issues/77")} - Release cursor: SHIFT+F12
toggle-fullscreen=shift+f11
release-cursor=shift+f12
secure-attention=ctrl+alt+end
tls-port=${TODO("https://github.com/kerubistan/kerub/issues/77")}
enable-smartcard=0
enable-usb-autoshare=1
tls-ciphers=DEFAULT
host-subject=O=engine,CN=192.168.122.71
ca=${TODO("https://github.com/kerubistan/kerub/issues/77")}
secure-channels=main;inputs;cursor;playback;record;display;smartcard;usbredir

"""
	}
}