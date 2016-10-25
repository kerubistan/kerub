Feature: support for cpu clock speed expectation

  Scenario: Start a virtual machine with cpu clockspeed expectation
	Given hosts:
	  | address           | ram  | Cores | Threads | Architecture | Operating System | Distro | Distro Vresion |
	  | host1.example.com | 6 GB | 2     | 4       | x86_64       | Linux            | CentOS | 7              |
	  | host2.example.com | 6 GB | 2     | 4       | x86_64       | Linux            | CentOS | 7              |
	And host host1.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags       |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc |
	And host host2.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags       |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc |
	And VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 1 GB   | 1 GB   | 2    | x86_64       |
	And vm1 has cpu clock speed expectation 2000 Mhz
	And software installed on host host1.example.com:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And software installed on host host2.example.com:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And host host1.example.com is Up
	And host host2.example.com is Up
	And host1.example.com cpu clockspeed is 1700 Mhz
	And host2.example.com cpu clockspeed is 2400 Mhz
	When VM vm1 is started
	Then VM vm1 gets scheduled on host host2.example.com with kvm hypervisor
