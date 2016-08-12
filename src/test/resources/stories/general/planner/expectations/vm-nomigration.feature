Feature: "No migration" expectation

  # this is a tricky scenario, because there is no explicit 'migrate command'
  # therefore the trick here is that there are two VM's running on host1
  # the third VM for a reason (memory need) can only be started on host1.
  # in this case one of the two running VM's must be selected for migration
  # and what the test verifies here is that the one without no-migrate
  # expectation was selected
  Scenario: vm without "No migration" expectation is preferred when selecting for migration
	Given hosts:
	  | address                | ram  | Cores | Threads | Architecture | Operating System |
	  | host-big.example.com   | 6 GB | 2     | 4       | x86_64       | Linux            |
	  | host-small.example.com | 3 GB | 2     | 4       | x86_64       | Linux            |
	And VMs:
	  | name      | MinRam | MaxRam | CPUs | Architecture |
	  | vm-sticky | 2 GB   | 2 GB   | 1    | x86_64       |
	  | vm-free   | 2 GB   | 2 GB   | 1    | x86_64       |
	  | vm-fat    | 4 GB   | 4 GB   | 1    | x86_64       |
	And software installed on host host-big.example.com:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And software installed on host host-small.example.com:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And host host-big.example.com is Up
	And host host-small.example.com is Up
	And vm-sticky is running on host-big.example.com
	And vm-free is running on host-big.example.com
	And vm-sticky has no-migrate expectation
	When VM vm-fat is started
	Then vm-free will be migrated to host-small.example.com as step 1
	And VM vm-fat gets scheduled on host host-big.example.com as step 2