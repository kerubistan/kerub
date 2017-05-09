Feature: dedicated cores

  Scenario: Start a vm without dedicated cores when a dedicated one is already running
	Given hosts:
	  | address           | ram  | Cores | Threads | Architecture | Operating System | Distro       | Distro Version |
	  | host1.example.com | 6 GB | 2     | 4       | x86_64       | Linux            | Centos Linux | 7              |
	  | host2.example.com | 6 GB | 2     | 4       | x86_64       | Linux            | Centos Linux | 7              |
	And software installed on host host1.example.com:
	  | package        | version |
	  | qemu-kvm       | 2.4.1   |
	  | libvirt-client | 1.2.18  |
	And software installed on host host2.example.com:
	  | package        | version |
	  | qemu-kvm       | 2.4.1   |
	  | libvirt-client | 1.2.18  |
	And host host1.example.com CPUs are 1:
	  | Manufacturer | Mhz  | Name       | Flags       | Cores | Threads |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc | 2     | 4       |
	And host host2.example.com CPUs are 1:
	  | Manufacturer | Mhz  | Name       | Flags       | Cores | Threads |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc | 2     | 4       |
	And VMs:
	  | name             | MinRam | MaxRam | CPUs | Architecture |
	  | vm-decicatedcore | 2 GB   | 2 GB   | 2    | x86_64       |
	  | vm-sharedcore    | 2 GB   | 2 GB   | 2    | x86_64       |
	And host host1.example.com is Up
	And host host2.example.com is Up
	And vm-decicatedcore requires dedicated cores
	And vm-decicatedcore is running on host1.example.com
	When VM vm-sharedcore is started
	Then VM vm-sharedcore gets scheduled on host host2.example.com with kvm hypervisor
 #because on host-1 there are not enough cores left


  Scenario: Start a vm with dedicated cores when a shared one is already running
	Given hosts:
	  | address           | ram  | Cores | Threads | Architecture | Operating System | Distro       | Distro Version |
	  | host1.example.com | 6 GB | 2     | 4       | x86_64       | Linux            | Centos Linux | 7              |
	  | host2.example.com | 6 GB | 2     | 4       | x86_64       | Linux            | Centos Linux | 7              |
	And software installed on host host1.example.com:
	  | package        | version |
	  | qemu-kvm       | 2.4.1   |
	  | libvirt-client | 1.2.18  |
	And software installed on host host2.example.com:
	  | package        | version |
	  | qemu-kvm       | 2.4.1   |
	  | libvirt-client | 1.2.18  |
	And host host1.example.com CPUs are 1:
	  | Manufacturer | Mhz  | Name       | Flags       | Cores | Threads |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc | 2     | 4       |
	And host host2.example.com CPUs are 1:
	  | Manufacturer | Mhz  | Name       | Flags       | Cores | Threads |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc | 2     | 4       |
	And VMs:
	  | name             | MinRam | MaxRam | CPUs | Architecture |
	  | vm-decicatedcore | 2 GB   | 2 GB   | 2    | x86_64       |
	  | vm-sharedcore    | 2 GB   | 2 GB   | 2    | x86_64       |
	And host host1.example.com is Up
	And host host2.example.com is Up
	And vm-decicatedcore requires dedicated cores
	And vm-sharedcore is running on host1.example.com
	When VM vm-decicatedcore is started
	Then VM vm-decicatedcore gets scheduled on host host2.example.com with kvm hypervisor
 # because on host-1 there there is a VM and not enough cores left

  #TODO: affinity should be tested here