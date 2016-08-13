Feature: basic planner features

  Scenario: Start a VM with single host
	Given VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 1GB    | 4GB    | 2    | x86_64       |
	And hosts:
	  | address   | ram | Cores | Threads | Architecture | Operating System |
	  | 127.0.0.5 | 2GB | 2     | 4       | x86_64       | Linux            |
	And software installed on host 127.0.0.5:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And host 127.0.0.5 is Up
	When VM vm1 is started
	Then VM vm1 gets scheduled on host 127.0.0.5 with kvm hypervisor

  Scenario: Start a VM with two hosts, one of them is does not match required architecture
	Given VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 1GB    | 2GB    | 2    | x86_64       |
	And hosts:
	  | address   | ram | Cores | Threads | Architecture | Operating System |
	  | 127.0.0.5 | 2GB | 2     | 4       | x86_64       | Linux            |
	  | 127.0.0.6 | 2GB | 2     | 2       | ARM          | Linux            |
	And software installed on host 127.0.0.5:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And software installed on host 127.0.0.6:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And host 127.0.0.5 is Up
	And host 127.0.0.6 is Up
	When VM vm1 is started
	Then VM vm1 gets scheduled on host 127.0.0.5 with kvm hypervisor

  Scenario: Start a VM with two hosts, one of them is does not match required amount of cores
	Given VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 1 GB   | 2 GB   | 2    | x86_64       |
	And hosts:
	  | address   | ram  | Cores | Threads | Architecture | Operating System |
	  | 127.0.0.5 | 2 GB | 2     | 4       | x86_64       | Linux            |
	  | 127.0.0.6 | 2 GB | 1     | 2       | x86_64       | Linux            |
	And software installed on host 127.0.0.5:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And software installed on host 127.0.0.6:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And host 127.0.0.5 is Up
	And host 127.0.0.6 is Up
	When VM vm1 is started
	Then VM vm1 gets scheduled on host 127.0.0.5 with kvm hypervisor

  Scenario: Start a VM with two hosts, one of them is does not match required amount of RAM
	Given VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 2 GB   | 2 GB   | 2    | x86_64       |
	And hosts:
	  | address   | ram  | Cores | Threads | Architecture | Operating System |
	  | 127.0.0.5 | 4 GB | 2     | 4       | x86_64       | Linux            |
	  | 127.0.0.6 | 1 GB | 2     | 4       | x86_64       | LInux            |
	And host 127.0.0.5 is Up
	And host 127.0.0.6 is Up
	And software installed on host 127.0.0.5:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And software installed on host 127.0.0.6:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	When VM vm1 is started
	Then VM vm1 gets scheduled on host 127.0.0.5 with kvm hypervisor

  Scenario: migration is needed to start the second VM
	Given VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
	  | vm2  | 8 GB   | 8 GB   | 2    | x86_64       |
	And hosts:
	  | address   | ram   | Cores | Threads | Architecture | Operating System |
	  | 127.0.0.5 | 6 GB  | 2     | 4       | x86_64       | Linux            |
	  | 127.0.0.6 | 10 GB | 2     | 4       | x86_64       | Linux            |
	And software installed on host 127.0.0.5:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And software installed on host 127.0.0.6:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And host 127.0.0.5 is Up
	And host 127.0.0.6 is Up
	And vm1 is running on 127.0.0.6
	When VM vm2 is started
	Then vm1 will be migrated to 127.0.0.5 as step 1
	And VM vm2 gets scheduled on host 127.0.0.6 as step 2

  Scenario: A host must be started in order to start the VM
	Given VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
	And hosts:
	  | address   | ram  | Cores | Threads | Architecture | Operating System |
	  | 127.0.0.5 | 6 GB | 2     | 4       | x86_64       | Linux            |
	And software installed on host 127.0.0.5:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And host 127.0.0.5 has wake-on-lan power management
	And host 127.0.0.5 is Down
	When VM vm1 is started
	Then 127.0.0.5 will be started as step 1
	And VM vm1 gets scheduled on host 127.0.0.5 as step 2

  Scenario: The virtual disk for the VM already exists
	Given VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
	And hosts:
	  | address   | ram  | Cores | Threads | Architecture | Operating System |
	  | 127.0.0.5 | 6 GB | 2     | 4       | x86_64       | Linux            |
	And virtual storage devices:
	  | name          | size | ro    |
	  | system-disk-1 | 2 GB | false |
	And software installed on host 127.0.0.5:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And host 127.0.0.5 is Up
	And system-disk-1 is attached to vm1
	And the virtual disk system-disk-1 is created on 127.0.0.5
	When VM vm1 is started
	Then the virtual disk system-disk-1 must not be allocated
	And VM vm1 gets scheduled on host 127.0.0.5

