package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.utils.createObjectMapper
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.ispn.InfinispanJsonExternalizer
import org.infinispan.configuration.cache.Configuration
import org.infinispan.configuration.global.GlobalConfiguration
import org.infinispan.configuration.parsing.ParserRegistry

class IspnConfiguration {
	var template = "infinispan.xml"
	var baseDir = "."
	var dynamicOwners = 1
	var staticOwners = 2
	var clusterName = "kerub"
	var rackId = "default-rack"
	var siteId = "default-site"
	var json = false

	companion object {
		val logger = getLogger(IspnConfiguration::class)
	}

	private var globalConfig: GlobalConfiguration? = null
	private var config: Configuration? = null

	fun init() {
		logger.info("ispn global configuration")
		logger.info("site id: {}", siteId)
		logger.info("cluster name: {}", clusterName)
		logger.info("rack id: {}", rackId)
		logger.info("storage directory: {}", baseDir)
		logger.info("static owners: {}", staticOwners)
		logger.info("dynamic owners: {}", dynamicOwners)
		logger.info("json: {}", json)


		//TODO: setting system property to configure ISPN is highly unfriendly
		// alternative needs to be investigated
		System.setProperty("store.dir", baseDir)
		System.setProperty("dyn.owners", dynamicOwners.toString())
		System.setProperty("stat.owners", staticOwners.toString())

		val template = loadTemplate()
		val globalConfigBuilder = template.globalConfigurationBuilder

		globalConfigBuilder.transport().clusterName(clusterName).rackId(rackId).siteId(siteId)
		if (json) {
			globalConfigBuilder.serialization().addAdvancedExternalizer(6665, InfinispanJsonExternalizer())
			globalConfigBuilder.serialization().marshaller(JsonMarshaller(createObjectMapper(prettyPrint = false)))
		}

		val configBuilder = template.currentConfigurationBuilder
		globalConfig = globalConfigBuilder.build()
		config = configBuilder.build(globalConfig)
	}

	fun build(): Configuration {
		return config!!
	}

	fun buildGlobal(): GlobalConfiguration {
		return globalConfig!!
	}

	internal fun loadTemplate() =
			Thread.currentThread().contextClassLoader.getResourceAsStream(template).use {
				ParserRegistry().parse(it)
			}

}