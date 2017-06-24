package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.site.SiteFeature

@JsonTypeName("site-features")
data class SiteFeaturesExpectation(
		override val level: ExpectationLevel,
		val features: List<SiteFeature>
) : VirtualMachineExpectation, VirtualStorageExpectation, VirtualNetworkExpectation

