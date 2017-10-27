package com.github.kerubistan.kerub.utils.ispn

import com.github.kerubistan.kerub.testVm
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ObjectOutput

class InfinispanJsonExternalizerTest {
	@Test
	fun getTypeClasses() {
		assertTrue(InfinispanJsonExternalizer().typeClasses.isNotEmpty())
	}

	@Test
	fun writeObject() {
		val output : ObjectOutput = mock()
		InfinispanJsonExternalizer().writeObject(output, testVm)
	}

	@Test
	fun readObject() {

	}

}