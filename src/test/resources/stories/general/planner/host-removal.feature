Feature: removing a host

  Scenario: Start a VM with two hosts, one of these is being recycled
	Given VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 1GB    | 4GB    | 2    | x86_64       |
	And hosts:
	  | address                | ram | Cores | Threads | Architecture | Operating System | Distro | Distro version |
	  | diebastard.example.com | 2GB | 2     | 4       | x86_64       | Linux            | Ubuntu | 14.0.4         |
	  | stayalive.example.com  | 2GB | 2     | 4       | x86_64       | Linux            | Ubuntu | 14.0.4         |
	And host diebastard.example.com is scheduled for recycling
	And software installed on host stayalive.example.com:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And host stayalive.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags       |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc |
	And host stayalive.example.com is Up
 #exactly like stayalive.example.com
	And software installed on host diebastard.example.com:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And host diebastard.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags       |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc |
	And host diebastard.example.com is Up

	When VM vm1 is started
	Then VM vm1 gets scheduled on host stayalive.example.com with kvm hypervisor
 #and not on diebastard.example.com, that is the point