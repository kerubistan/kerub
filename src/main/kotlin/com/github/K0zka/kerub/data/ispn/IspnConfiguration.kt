package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.utils.createObjectMapper
import org.infinispan.configuration.cache.Configuration
import org.infinispan.configuration.cache.ConfigurationBuilder
import org.infinispan.configuration.global.GlobalConfiguration
import org.infinispan.configuration.parsing.ParserRegistry

class IspnConfiguration {
	var template = "infinispan.xml"
	var baseDir = ""
	var dynamicOwners = 1
	var staticOwners = 2
	fun build() : Configuration {
		var configBuilder = loadTemplate().getCurrentConfigurationBuilder()
		return configBuilder.build()
	}

	fun buildGlobal() : GlobalConfiguration {
		var configBuilder = loadTemplate().getGlobalConfigurationBuilder()
		//configBuilder.serialization().marshaller(JsonMarshaller(createObjectMapper()))
		return configBuilder.build()
	}

	fun loadTemplate() =
		Thread.currentThread().getContextClassLoader().getResourceAsStream(template).use {
			ParserRegistry().parse(it)
		}

}