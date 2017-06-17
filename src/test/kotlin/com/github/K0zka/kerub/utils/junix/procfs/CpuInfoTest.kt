package com.github.K0zka.kerub.utils.junix.procfs

import com.github.K0zka.kerub.utils.resource
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CpuInfoTest {

	val session: ClientSession = mock()
	val openFuture: OpenFuture = mock()
	val sftp: SftpClient = mock()

	@Test
	fun listOnPpc() {
		whenever(session.createSftpClient()).thenReturn(sftp)
		whenever(sftp.read(eq("/proc/cpuinfo"))).thenReturn(
				resource("com/github/K0zka/kerub/utils/junix/procfs/cpuinfo-linux-power.txt")
		)

		val results = CpuInfo.listPpc(session)
		assertEquals(2, results.size)
	}

	@Test
	fun list() {
		whenever(session.createSftpClient()).thenReturn(sftp)
		whenever(sftp.read(eq("/proc/cpuinfo"))).thenReturn(
				resource("com/github/K0zka/kerub/utils/junix/procfs/cpuinfo-linux-i7.txt")
		)

		val results = CpuInfo.list(session)

		assertEquals(4, results.size)
		assertTrue(results.all { it.vendorId == "GenuineIntel" })
		assertTrue(results.all { it.modelName == "Intel(R) Core(TM) i7-5500U CPU @ 2.40GHz" })
		assertTrue(results.all {
			it.flags == listOf(
					"fpu", "vme", "de", "pse", "tsc", "msr", "pae", "mce", "cx8", "apic", "sep", "mtrr", "pge", "mca",
					"cmov", "pat", "pse36", "clflush", "dts", "acpi", "mmx", "fxsr", "sse", "sse2", "ss", "ht", "tm",
					"pbe", "syscall", "nx", "pdpe1gb", "rdtscp", "lm", "constant_tsc", "arch_perfmon", "pebs", "bts",
					"rep_good", "nopl", "xtopology", "nonstop_tsc", "aperfmperf", "eagerfpu", "pni", "pclmulqdq",
					"dtes64", "monitor", "ds_cpl", "vmx", "est", "tm2", "ssse3", "sdbg", "fma", "cx16", "xtpr", "pdcm",
					"pcid", "sse4_1", "sse4_2", "x2apic", "movbe", "popcnt", "tsc_deadline_timer", "aes", "xsave",
					"avx", "f16c", "rdrand", "lahf_lm", "abm", "3dnowprefetch", "epb", "intel_pt", "tpr_shadow",
					"vnmi", "flexpriority", "ept", "vpid", "fsgsbase", "tsc_adjust", "bmi1", "avx2", "smep", "bmi2",
					"erms", "invpcid", "rdseed", "adx", "smap", "xsaveopt", "dtherm", "ida", "arat", "pln", "pts")
		})
	}
}