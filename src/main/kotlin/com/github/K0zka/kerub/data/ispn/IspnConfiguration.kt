package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.utils.getLogger
import org.infinispan.configuration.cache.Configuration
import org.infinispan.configuration.cache.SingleFileStoreConfigurationBuilder
import org.infinispan.configuration.global.GlobalConfiguration
import org.infinispan.configuration.parsing.ParserRegistry

class IspnConfiguration {
	var template = "infinispan.xml"
	var baseDir = ""
	var dynamicOwners = 1
	var staticOwners = 2
	var clusterName = "kerub"
	var rackId = "default-rack"
	var siteId = "default-site"

	companion object {
		val logger = getLogger(IspnConfiguration::class)
	}

	var globalConfig : GlobalConfiguration? = null
	var config : Configuration? = null

	fun init() {
		val template = loadTemplate()
		var globalConfigBuilder = template.getGlobalConfigurationBuilder()
		logger.info("ispn global configuration")
		logger.info("site id: {}", siteId)
		logger.info("cluster name: {}", clusterName)
		logger.info("rack id: {}", rackId)
		globalConfigBuilder.transport().clusterName(clusterName).rackId(rackId).siteId(siteId)

		var configBuilder = template.getCurrentConfigurationBuilder()
		configBuilder.eviction().persistence().stores().forEach {
			when(it) {
				is SingleFileStoreConfigurationBuilder -> {
					logger.info("file store")
				}
			}
		}
		globalConfig = globalConfigBuilder.build()
		config = configBuilder.build(globalConfig)
	}

	fun build() : Configuration {
		return config!!
	}

	fun buildGlobal() : GlobalConfiguration {
		return globalConfig!!
	}

	fun loadTemplate() =
		Thread.currentThread().getContextClassLoader().getResourceAsStream(template).use {
			ParserRegistry().parse(it)
		}

}