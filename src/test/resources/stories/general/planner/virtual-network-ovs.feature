Feature: Virtual networking with openVSwitch

  Scenario Outline: start two vms sharing one network on the same host
	Given hosts:
	  | address            | ram    | Cores | Threads | Architecture | Operating System | Distribution | Dist. Version |
	  | host-1.example.com | 128 GB | 2     | 4       | x86_64       | <OS>             | <distro>     | <version>     |
	And host host-1.example.com is Up
	And host host-1.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags   |
	  | GenuineIntel | 2400 | Intel Xeon | sse,vmx |
	And software installed on host host-1.example.com:
	  | package           | version |
	  | qemu-kvm          | 2.4.1   |
	  | <libvirt-package> | 1.2.18  |
	And virtual networks:
	  | name   |
	  | vnet-1 |
	And VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
	  | vm2  | 4 GB   | 4 GB   | 2    | x86_64       |
	And VM vm1 has NIC of type virtio connected to network vnet-1
	And VM vm2 has NIC of type virtio connected to network vnet-1
	When VMs vm1,vm2 are started
	Then ovs switch will be created on host host-1.example.com for network vnet-1
	And ovs port will be created on host host-1.example.com on network vnet-1 for vm1
	And ovs port will be created on host host-1.example.com on network vnet-1 for vm2
	And VM vm1 gets scheduled on host host-1.example.com
	And VM vm2 gets scheduled on host host-1.example.com

	Examples:
	  | OS    | distro       | version | libvirt-package |
	  | Linux | CentOS Linux | 7.1     | libvirt-client  |
	  | Linux | openSUSE     | 42.0    | libvirt-client  |
	  | Linux | Ubuntu       | 18.04   | libvirt-bin     |
