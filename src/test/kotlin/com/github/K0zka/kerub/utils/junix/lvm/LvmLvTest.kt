package com.github.K0zka.kerub.utils.junix.lvm

import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.ClientSession
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.math.BigInteger

@RunWith(MockitoJUnitRunner::class)
class LvmLvTest {

	@Mock
	var session : ClientSession? = null

	@Mock
	var execChannel: ChannelExec? = null

	@Mock
	var openFuture: OpenFuture? = null

	val testListOutput =
			"""  Mvd5u6-pTbR-SUS2-sd2l-kx41-a0bx-YGuWcK,root,/dev/fedora/root,9135194112B,,
  eCuTKA-rIDz-dzJq-48pK-DtqJ-X77p-YStcLS,testlv1,/dev/test/testlv1,1073741824B,,
  U3xf04-DJFM-nXD5-682c-9w32-rcjS-pne6t2,testlv2,/dev/test/testlv2,1073741824B,,"""


	@Test
	fun list() {

		Mockito.`when`(session?.createExecChannel(Matchers.startsWith("lvs"))).thenReturn(execChannel)
		Mockito.`when`(execChannel?.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel?.invertedOut).thenReturn(ByteArrayInputStream(testListOutput.toByteArray("ASCII")))
		Mockito.`when`(execChannel?.invertedErr).thenReturn(NullInputStream(0))

		val list = LvmLv.list(session!!)

		Assert.assertEquals(3, list.size)
		Assert.assertEquals("Mvd5u6-pTbR-SUS2-sd2l-kx41-a0bx-YGuWcK",list[0].id)
		Assert.assertEquals("root",list[0].name)
		Assert.assertEquals("/dev/fedora/root",list[0].path)
		Assert.assertEquals(BigInteger("9135194112"),list[0].size)
	}

	@Test
	fun delete() {
		Mockito.`when`(session?.createExecChannel(Matchers.startsWith("lvremove"))).thenReturn(execChannel)
		Mockito.`when`(execChannel?.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel?.invertedOut).thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel?.invertedErr).thenReturn(NullInputStream(0))

		LvmLv.delete(session!!, "test")
	}

	@Test(expected = IOException::class)
	fun deleteAndFail() {
		Mockito.`when`(session?.createExecChannel(Matchers.startsWith("lvremove"))).thenReturn(execChannel)
		Mockito.`when`(execChannel?.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel?.invertedOut).thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel?.invertedErr).thenReturn(ByteArrayInputStream(""" Volume group "test" not found
  Cannot process volume group test
""".toByteArray("ASCII")))

		LvmLv.delete(session!!, "test")

	}

}