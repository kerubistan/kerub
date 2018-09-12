Feature: support for host cpu cache-size expectation

  Scenario: host selection with cache information
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
	And host1.example.com manufaturer has 512 KB L1 cache
	And host1.example.com manufaturer has 1024 KB L2 cache
	And host2.example.com manufaturer has NO L1 cache
	And host2.example.com manufaturer has NO L2 cache
	And software installed on host host2.example.com:
	  | package        | version |
	  | qemu-kvm       | 2.4.1   |
	  | libvirt-client | 1.2.18  |
	And software installed on host host1.example.com:
	  | package        | version |
	  | qemu-kvm       | 2.4.1   |
	  | libvirt-client | 1.2.18  |
	And host host1.example.com is Up
	And host host2.example.com is Up
	And VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
	And VM vm1 requires 512 KB L1 cache
	When VM vm1 is started
	Then VM vm1 gets scheduled on host host1.example.com with kvm hypervisor

  Scenario: host selection with cache information, one host with too few cache, one with just enough
	Given hosts:
	  | address           | ram  | Cores | Threads | Architecture | Operating System | Distro       | Distro Version |
	  | host0.example.com | 6 GB | 2     | 4       | x86_64       | Linux            | CentOS Linux | 7              |
	  | host1.example.com | 6 GB | 2     | 4       | x86_64       | Linux            | CentOS Linux | 7              |
	  | host2.example.com | 6 GB | 2     | 4       | x86_64       | Linux            | CentOS Linux | 7              |
	And host host0.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags       |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc |
	And host host1.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags       |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc |
	And host host2.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags       |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc |
 #too few cache
	And host0.example.com manufaturer has 128 KB L1 cache
	And host0.example.com manufaturer has 256 KB L2 cache
 #just enough, this host should be preferred
	And host1.example.com manufaturer has 512 KB L1 cache
	And host1.example.com manufaturer has 1024 KB L2 cache
 #no cache at all, not good
	And host2.example.com manufaturer has NO L1 cache
	And host2.example.com manufaturer has NO L2 cache
	And software installed on host host9.example.com:
	  | package        | version |
	  | qemu-kvm       | 2.4.1   |
	  | libvirt-client | 1.2.18  |
	And software installed on host host1.example.com:
	  | package        | version |
	  | qemu-kvm       | 2.4.1   |
	  | libvirt-client | 1.2.18  |
	And software installed on host host2.example.com:
	  | package        | version |
	  | qemu-kvm       | 2.4.1   |
	  | libvirt-client | 1.2.18  |
	And host host0.example.com is Up
	And host host1.example.com is Up
	And host host2.example.com is Up
	And VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
	And VM vm1 requires 512 KB L1 cache
	When VM vm1 is started
	Then VM vm1 gets scheduled on host host1.example.com with kvm hypervisor