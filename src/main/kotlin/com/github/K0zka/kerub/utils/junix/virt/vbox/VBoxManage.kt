package com.github.K0zka.kerub.utils.junix.virt.vbox

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.io.DeviceType
import com.github.K0zka.kerub.model.io.VirtualDiskFormat
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

object VBoxManage {
	fun startVm(session: ClientSession, vm : VirtualMachine) {
		val vmDescriptor = """
<?xml version="1.0"?>
<VirtualBox xmlns="http://www.innotek.de/VirtualBox-settings" version="1.14-freebsd">
  <Machine uuid="{${vm.id}}" name="${vm.id}" OSType="Other" snapshotFolder="Snapshots" lastStateChange="2016-07-31T15:14:04Z">
    <MediaRegistry>
      <HardDisks/>
      <DVDImages/>
      <FloppyImages/>
    </MediaRegistry>
    <Hardware version="2">
      <CPU count="1" hotplug="false">
        <HardwareVirtEx enabled="true"/>
        <HardwareVirtExNestedPaging enabled="true"/>
        <HardwareVirtExVPID enabled="true"/>
        <HardwareVirtExUX enabled="true"/>
        <PAE enabled="true"/>
        <LongMode enabled="true"/>
        <HardwareVirtExLargePages enabled="true"/>
        <HardwareVirtForce enabled="false"/>
      </CPU>
      <Memory RAMSize="128" PageFusion="false"/>
      <HID Pointing="PS2Mouse" Keyboard="PS2Keyboard"/>
      <HPET enabled="false"/>
      <Chipset type="PIIX3"/>
      <Boot>
        <Order position="1" device="DVD"/>
        <Order position="2" device="HardDisk"/>
        <Order position="3" device="None"/>
      </Boot>
      <Display VRAMSize="8" monitorCount="1" accelerate3D="false" accelerate2DVideo="false"/>
      <VideoCapture enabled="false" screens="18446744073709551615" horzRes="1024" vertRes="768" rate="512" fps="25"/>
      <RemoteDisplay enabled="false" authType="Null"/>
      <BIOS>
        <ACPI enabled="true"/>
        <IOAPIC enabled="false"/>
        <Logo fadeIn="true" fadeOut="true" displayTime="0"/>
        <BootMenu mode="MessageAndMenu"/>
        <TimeOffset value="0"/>
        <PXEDebug enabled="false"/>
      </BIOS>
      <USB>
        <Controllers/>
        <DeviceFilters/>
      </USB>
      <Network>
      </Network>
      <UART>
        <Port slot="0" enabled="false" IOBase="0x3f8" IRQ="4" hostMode="Disconnected"/>
        <Port slot="1" enabled="false" IOBase="0x3f8" IRQ="4" hostMode="Disconnected"/>
      </UART>
      <LPT>
        <Port slot="0" enabled="false" IOBase="0x378" IRQ="7"/>
        <Port slot="1" enabled="false" IOBase="0x378" IRQ="7"/>
      </LPT>
      <AudioAdapter controller="AC97" driver="OSS" enabled="false"/>
      <RTC localOrUTC="local"/>
      <SharedFolders/>
      <Clipboard mode="Disabled"/>
      <DragAndDrop mode="Disabled"/>
      <IO>
        <IoCache enabled="true" size="5"/>
        <BandwidthGroups/>
      </IO>
      <HostPci>
        <Devices/>
      </HostPci>
      <EmulatedUSB>
        <CardReader enabled="false"/>
      </EmulatedUSB>
      <Guest memoryBalloonSize="0"/>
      <GuestProperties/>
    </Hardware>
    <StorageControllers/>
  </Machine>
</VirtualBox>
		"""
		TODO("https://github.com/kerubistan/kerub/issues/93")
	}
	fun stopVm(session: ClientSession) {
		TODO("https://github.com/kerubistan/kerub/issues/93")
	}
	fun createMedium(session: ClientSession, path : String, size : BigInteger, type: DeviceType, format: VirtualDiskFormat) {
		session.executeOrDie("VBoxManage create $type --filename $path --format $format")
	}

}

