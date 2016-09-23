package com.github.K0zka.kerub.utils.junix.lspci

import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.io.ByteArrayInputStream

class LsPciTest {

	val session : ClientSession = mock()
	val execChannel : ChannelExec = mock()
	val channelOpenFuture : OpenFuture = mock()

	object samples {
		val nuc =
				"""00:00.0 "Host bridge" "Intel Corporation" "Haswell-ULT DRAM Controller" -r09 "Intel Corporation" "Device 2054"
00:02.0 "VGA compatible controller" "Intel Corporation" "Haswell-ULT Integrated Graphics Controller" -r09 "Intel Corporation" "Device 2054"
00:03.0 "Audio device" "Intel Corporation" "Haswell-ULT HD Audio Controller" -r09 "Intel Corporation" "Device 2054"
00:14.0 "USB controller" "Intel Corporation" "8 Series USB xHCI HC" -r04 -p30 "Intel Corporation" "Device 2054"
00:16.0 "Communication controller" "Intel Corporation" "8 Series HECI #0" -r04 "Intel Corporation" "Device 2054"
00:19.0 "Ethernet controller" "Intel Corporation" "Ethernet Connection I218-V" -r04 "Intel Corporation" "Device 2054"
00:1d.0 "USB controller" "Intel Corporation" "8 Series USB EHCI #1" -r04 -p20 "Intel Corporation" "Device 2054"
00:1f.0 "ISA bridge" "Intel Corporation" "8 Series LPC Controller" -r04 "Intel Corporation" "Device 2054"
00:1f.2 "SATA controller" "Intel Corporation" "8 Series SATA Controller 1 [AHCI mode]" -r04 -p01 "Intel Corporation" "Device 2054"
00:1f.3 "SMBus" "Intel Corporation" "8 Series SMBus Controller" -r04 "Intel Corporation" "Device 2054"
"""
		val mylaptop =
				"""00:00.0 "Host bridge" "Advanced Micro Devices, Inc. [AMD]" "Family 14h Processor Root Complex" "Advanced Micro Devices, Inc. [AMD]" "Family 14h Processor Root Complex"
00:01.0 "VGA compatible controller" "Advanced Micro Devices, Inc. [AMD/ATI]" "Wrestler [Radeon HD 7340]" "Lenovo" "Device 3972"
00:01.1 "Audio device" "Advanced Micro Devices, Inc. [AMD/ATI]" "Wrestler HDMI Audio" "Lenovo" "Device 397f"
00:04.0 "PCI bridge" "Advanced Micro Devices, Inc. [AMD]" "Family 14h Processor Root Port" "" ""
00:10.0 "USB controller" "Advanced Micro Devices, Inc. [AMD]" "FCH USB XHCI Controller" -r03 -p30 "Lenovo" "Device 397f"
00:11.0 "SATA controller" "Advanced Micro Devices, Inc. [AMD]" "FCH SATA Controller [AHCI mode]" -p01 "Lenovo" "Device 397f"
00:12.0 "USB controller" "Advanced Micro Devices, Inc. [AMD]" "FCH USB OHCI Controller" -r11 -p10 "Lenovo" "Device 397f"
00:12.2 "USB controller" "Advanced Micro Devices, Inc. [AMD]" "FCH USB EHCI Controller" -r11 -p20 "Lenovo" "Device 397f"
00:13.0 "USB controller" "Advanced Micro Devices, Inc. [AMD]" "FCH USB OHCI Controller" -r11 -p10 "Lenovo" "Device 397f"
00:13.2 "USB controller" "Advanced Micro Devices, Inc. [AMD]" "FCH USB EHCI Controller" -r11 -p20 "Lenovo" "Device 397f"
00:14.0 "SMBus" "Advanced Micro Devices, Inc. [AMD]" "FCH SMBus Controller" -r14 "Lenovo" "Device 397f"
00:14.2 "Audio device" "Advanced Micro Devices, Inc. [AMD]" "FCH Azalia Controller" -r01 "Lenovo" "Device 397f"
00:14.3 "ISA bridge" "Advanced Micro Devices, Inc. [AMD]" "FCH LPC Bridge" -r11 "Lenovo" "Device 397f"
00:14.4 "PCI bridge" "Advanced Micro Devices, Inc. [AMD]" "FCH PCI Bridge" -r40 -p01 "" ""
00:15.0 "PCI bridge" "Advanced Micro Devices, Inc. [AMD]" "Hudson PCI to PCI bridge (PCIE port 0)" "" ""
00:15.1 "PCI bridge" "Advanced Micro Devices, Inc. [AMD]" "Hudson PCI to PCI bridge (PCIE port 1)" "" ""
00:18.0 "Host bridge" "Advanced Micro Devices, Inc. [AMD]" "Family 12h/14h Processor Function 0" -r43 "" ""
00:18.1 "Host bridge" "Advanced Micro Devices, Inc. [AMD]" "Family 12h/14h Processor Function 1" "" ""
00:18.2 "Host bridge" "Advanced Micro Devices, Inc. [AMD]" "Family 12h/14h Processor Function 2" "" ""
00:18.3 "Host bridge" "Advanced Micro Devices, Inc. [AMD]" "Family 12h/14h Processor Function 3" "" ""
00:18.4 "Host bridge" "Advanced Micro Devices, Inc. [AMD]" "Family 12h/14h Processor Function 4" "" ""
00:18.5 "Host bridge" "Advanced Micro Devices, Inc. [AMD]" "Family 12h/14h Processor Function 6" "" ""
00:18.6 "Host bridge" "Advanced Micro Devices, Inc. [AMD]" "Family 12h/14h Processor Function 5" "" ""
00:18.7 "Host bridge" "Advanced Micro Devices, Inc. [AMD]" "Family 12h/14h Processor Function 7" "" ""
01:00.0 "VGA compatible controller" "Advanced Micro Devices, Inc. [AMD/ATI]" "Robson CE [Radeon HD 6370M/7370M]" -rff -pff "" ""
03:00.0 "Ethernet controller" "Realtek Semiconductor Co., Ltd." "RTL8101E/RTL8102E PCI Express Fast Ethernet controller" -r05 "Lenovo" "Device 397f"
07:00.0 "Network controller" "Broadcom Corporation" "BCM4313 802.11bgn Wireless Network Adapter" -r01 "Broadcom Corporation" "Device 0587"
"""
		val kvm =
				"""00:00.0 "Host bridge" "Intel Corporation" "440FX - 82441FX PMC [Natoma]" -r02 "Red Hat, Inc" "Qemu virtual machine"
00:01.0 "ISA bridge" "Intel Corporation" "82371SB PIIX3 ISA [Natoma/Triton II]" "Red Hat, Inc" "Qemu virtual machine"
00:01.1 "IDE interface" "Intel Corporation" "82371SB PIIX3 IDE [Natoma/Triton II]" -p80 "Red Hat, Inc" "Qemu virtual machine"
00:01.3 "Bridge" "Intel Corporation" "82371AB/EB/MB PIIX4 ACPI" -r03 "Red Hat, Inc" "Qemu virtual machine"
00:02.0 "VGA compatible controller" "Red Hat, Inc." "QXL paravirtual graphic card" -r04 "Red Hat, Inc" "QEMU Virtual Machine"
00:03.0 "Ethernet controller" "Red Hat, Inc" "Virtio network device" "Red Hat, Inc" "Device 0001"
00:04.0 "Audio device" "Intel Corporation" "82801FB/FBM/FR/FW/FRW (ICH6 Family) High Definition Audio Controller" -r01 "Red Hat, Inc" "QEMU Virtual Machine"
00:05.0 "USB controller" "Intel Corporation" "82801I (ICH9 Family) USB UHCI Controller #1" -r03 "Red Hat, Inc" "QEMU Virtual Machine"
00:05.1 "USB controller" "Intel Corporation" "82801I (ICH9 Family) USB UHCI Controller #2" -r03 "Red Hat, Inc" "QEMU Virtual Machine"
00:05.2 "USB controller" "Intel Corporation" "82801I (ICH9 Family) USB UHCI Controller #3" -r03 "Red Hat, Inc" "QEMU Virtual Machine"
00:05.7 "USB controller" "Intel Corporation" "82801I (ICH9 Family) USB2 EHCI Controller #1" -r03 -p20 "Red Hat, Inc" "QEMU Virtual Machine"
00:06.0 "Communication controller" "Red Hat, Inc" "Virtio console" "Red Hat, Inc" "Device 0003"
00:07.0 "SCSI storage controller" "Red Hat, Inc" "Virtio block device" "Red Hat, Inc" "Device 0002"
00:08.0 "Unclassified device [00ff]" "Red Hat, Inc" "Virtio memory balloon" "Red Hat, Inc" "Device 0005"
"""
	}

	@Test
	fun parseWithKvm() {
		val devices = LsPci.parse(samples.kvm)
		Assert.assertThat(devices.size, CoreMatchers.`is`(14))
	}

	@Test
	fun parseWithMylaptop() {
		val devices = LsPci.parse(samples.mylaptop)
		Assert.assertThat(devices.size, CoreMatchers.`is`(27))
	}

	@Test
	fun parseWithNuc() {
		val devices = LsPci.parse(samples.nuc)
		Assert.assertThat(devices.size, CoreMatchers.`is`(10))
	}

	@Test
	fun parseLine() {
		val device = LsPci.parseLine("""00:03.0 "Ethernet controller" "Red Hat, Inc" "Virtio network device" "Red Hat, Inc" "Device 0001"""")
		Assert.assertThat(device.address, CoreMatchers.`is`("00:03.0"))
		Assert.assertThat(device.devClass, CoreMatchers.`is`("Ethernet controller"))
		Assert.assertThat(device.vendor, CoreMatchers.`is`("Red Hat, Inc"))
		Assert.assertThat(device.device, CoreMatchers.`is`("Virtio network device"))
	}

	@Test
	fun executeWithNuc() {
		val input = ByteArrayInputStream(samples.nuc.toByteArray(charset("ASCII")))
		whenever(session.createExecChannel(eq("lspci -mm")))
			.thenReturn(execChannel)
		whenever(execChannel.invertedOut).thenReturn(input)
		whenever(execChannel.open()).thenReturn(channelOpenFuture)

		val devices = LsPci.execute(session)
		Assert.assertThat(devices.size, CoreMatchers.`is`(10))
	}
}