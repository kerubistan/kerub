package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.Host
import java.util.UUID
import javax.ws.rs.Path
import javax.ws.rs.GET
import java.security.PublicKey
import javax.ws.rs.QueryParam
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation

Api("s/r/host", description = "Host service")
Path("/host")
public trait HostService : Listable<Host>, RestCrud<Host> {
	ApiOperation("Get the public key of the server", httpMethod = "GET")
	GET
	Path("/helpers/pubkey")
	fun getHostPubkey(QueryParam("address") address : String) : HostPubKey
}