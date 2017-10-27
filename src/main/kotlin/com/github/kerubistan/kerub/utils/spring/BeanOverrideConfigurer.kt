package com.github.kerubistan.kerub.utils.spring

import com.github.kerubistan.kerub.utils.filterByKeys
import com.github.kerubistan.kerub.utils.getLogger
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.config.PropertyOverrideConfigurer
import java.util.Properties

class BeanOverrideConfigurer : PropertyOverrideConfigurer() {

	companion object {
		private val log = getLogger(BeanOverrideConfigurer::class)
		private val classOverideSuffix = ".class"
		private val classOverrideFilter: (String) -> Boolean = { it.endsWith(classOverideSuffix) }
		private val propertyOverrideFilter: (String) -> Boolean = { !classOverrideFilter(it) }
	}

	override fun processProperties(beanFactory: ConfigurableListableBeanFactory, props: Properties) {
		val propOverrides = props.filterByKeys(propertyOverrideFilter)
		props.filterByKeys(classOverrideFilter).forEach { key, value ->
			val beanName = key.toString().substringBefore(classOverideSuffix)
			beanFactory.getBeanDefinition(beanName).let { def ->
				log.info("override class {} to {}", def.beanClassName, value)
				def.beanClassName = value.toString()
				//the old values no longer apply
				def.propertyValues.propertyValueList.clear()
			}

		}
		super.processProperties(beanFactory, propOverrides)
	}

}