package com.github.kerubistan.kerub.utils.spring

import com.github.kerubistan.kerub.utils.propertiesOf
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertTrue
import org.junit.Test
import org.springframework.beans.MutablePropertyValues
import org.springframework.beans.PropertyValue
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory

class BeanOverrideConfigurerTest {
	@Test
	fun processProperties() {
		val cfgr = BeanOverrideConfigurer()
		cfgr.setProperties(
				propertiesOf(
						"bean1.class" to "com.foo.bar.Enterprise"
				)
		)
		val beanFactory = mock<ConfigurableListableBeanFactory>()
		val beanDefinition = mock<BeanDefinition>()
		whenever(beanFactory.getBeanDefinition("bean1")).thenReturn(beanDefinition)
		val propertyValues = mock<MutablePropertyValues>()
		whenever(beanDefinition.propertyValues).thenReturn(propertyValues)
		val props = mutableListOf(PropertyValue("foo", "bar"))
		whenever(propertyValues.propertyValueList).thenReturn(props)
		cfgr.postProcessBeanFactory(beanFactory)

		assertTrue(props.isEmpty())
		verify(beanDefinition).beanClassName = eq("com.foo.bar.Enterprise")
	}

}