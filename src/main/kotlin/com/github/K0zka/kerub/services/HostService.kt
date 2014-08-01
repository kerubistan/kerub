package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.Host
import java.util.UUID
import javax.ws.rs.Path
import javax.ws.rs.GET
import java.security.PublicKey
import javax.ws.rs.QueryParam

Path("/host")
public trait HostService : Listable<Host>, RestCrud<Host, UUID> {
	GET
	Path("/helpers/pubkey")
	fun getHostPubkey(QueryParam("address") address : String) : HostPubKey
}