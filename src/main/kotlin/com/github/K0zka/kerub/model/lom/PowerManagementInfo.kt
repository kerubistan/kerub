package com.github.K0zka.kerub.model.lom

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(WakeOnLanInfo::class),
		JsonSubTypes.Type(IpmiInfo::class)
)
interface PowerManagementInfo : Serializable