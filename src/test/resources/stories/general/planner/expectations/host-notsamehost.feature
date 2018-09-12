Feature: support not-same-host expectation

  Scenario: Start a virtual machine with notsame expectation on it
	Given hosts:
	  | address           | ram  | Cores | Threads | Architecture | Operating System | Distro       | Distro Version |
	  | host1.example.com | 6 GB | 2     | 4       | x86_64       | Linux            | CentOS Linux | 7              |
	  | host2.example.com | 6 GB | 2     | 4       | x86_64       | Linux            | CentOS Linux | 7              |
	And host host1.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags       |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc |
	And host host2.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags       |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc |
	And VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 1 GB   | 1 GB   | 2    | x86_64       |
	  | vm2  | 1 GB   | 1 GB   | 2    | x86_64       |
	And software installed on host host1.example.com:
	  | package        | version |
	  | qemu-kvm       | 2.4.1   |
	  | libvirt-client | 1.2.18  |
	And software installed on host host2.example.com:
	  | package        | version |
	  | qemu-kvm       | 2.4.1   |
	  | libvirt-client | 1.2.18  |
	And host host1.example.com is Up
	And host host2.example.com is Up
	And vm2 is running on host2.example.com
	And vm1 has notsame host expectation against vm2
	When VM vm1 is started
	Then VM vm1 gets scheduled on host host1.example.com with kvm hypervisor


  Scenario: the other vm must be migrated to another host to be able to start
	Given hosts:
	  | address            | ram    | Cores | Threads | Architecture | Operating System | Distro       | Distro Version |
	  | host-s.example.com | 2 GB   | 2     | 4       | x86_64       | Linux            | CentOS Linux | 7              |
	  | host-b.example.com | 4.5 GB | 2     | 4       | x86_64       | Linux            | CentOS Linux | 7              |
	And host host-s.example.com CPUs are 2:
	  | Manufacturer | Mhz  | Name       | Flags       |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc |
	And host host-b.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags       |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc |
	And VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm-s | 1 GB   | 1 GB   | 2    | x86_64       |
	  | vm-b | 4 GB   | 4 GB   | 2    | x86_64       |
	And software installed on host host-s.example.com:
	  | package        | version |
	  | qemu-kvm       | 2.4.1   |
	  | libvirt-client | 1.2.18  |
	And software installed on host host-b.example.com:
	  | package        | version |
	  | qemu-kvm       | 2.4.1   |
	  | libvirt-client | 1.2.18  |
	And host host-s.example.com is Up
	And host host-b.example.com is Up
	And vm-s is running on host-b.example.com
	And vm-s has notsame host expectation against vm-b
	When VM vm-b is started
	Then vm-s will be migrated to host-s.example.com as step 1
	And VM vm-b gets scheduled on host host-b.example.com as step 2

