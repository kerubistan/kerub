package com.github.kerubistan.kerub.host.servicemanager.rc

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RcServiceManagerTest {

	@Test
	fun disable() {
		assertFalse(RcServiceManager.disable("ctld","""
hostname="freebsd10"
ifconfig_re0="DHCP"
ifconfig_re0_ipv6="inet6 accept_rtadv"
sshd_enable="YES"
ntpd_enable="YES"
# Set dumpdev to "AUTO" to enable crash dumps, "NO" to disable
dumpdev="AUTO"
ctld_enable="YES"
		""").contains("ctld"))
	}

	@Test
	fun testIsEnabled() {
		assertTrue(RcServiceManager.isEnabled("ctld",
				"""
hostname="freebsd10"
ifconfig_re0="DHCP"
ifconfig_re0_ipv6="inet6 accept_rtadv"
sshd_enable="YES"
ntpd_enable="YES"
# Set dumpdev to "AUTO" to enable crash dumps, "NO" to disable
dumpdev="AUTO"
ctld_enable="YES"
				"""))
		assertFalse(RcServiceManager.isEnabled("ctld",
				"""
hostname="freebsd10"
ifconfig_re0="DHCP"
ifconfig_re0_ipv6="inet6 accept_rtadv"
sshd_enable="YES"
ntpd_enable="YES"
# Set dumpdev to "AUTO" to enable crash dumps, "NO" to disable
dumpdev="AUTO"
ctld_enable="NO"
				"""))
		assertFalse(RcServiceManager.isEnabled("ctld",
				"""
hostname="freebsd10"
ifconfig_re0="DHCP"
ifconfig_re0_ipv6="inet6 accept_rtadv"
sshd_enable="YES"
ntpd_enable="YES"
# Set dumpdev to "AUTO" to enable crash dumps, "NO" to disable
dumpdev="AUTO"
				"""))
	}

	@Test
	fun testDisable() {

	}
}