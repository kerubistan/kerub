Feature: support for host manufacturer expectation

  Scenario: host selection with manufacturer info
	Given hosts:
	  | address           | ram  | Cores | Threads | Architecture | Operating System |
	  | host1.example.com | 6 GB | 2     | 4       | x86_64       | Linux            |
	  | host2.example.com | 6 GB | 2     | 4       | x86_64       | Linux            |
	And host1.example.com manufaturer is awesomehardware
	And host2.example.com manufaturer is enterprisejunkyard
	And host host1.example.com is Up
	And host host2.example.com is Up
	And software installed on host host1.example.com:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And software installed on host host2.example.com:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
	And VM vm1 requires manufacturer enterprisejunkyard
	When VM vm1 is started
	Then VM vm1 gets scheduled on host host2.example.com with kvm hypervisor