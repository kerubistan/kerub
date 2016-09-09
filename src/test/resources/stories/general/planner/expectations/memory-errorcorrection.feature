Feature: support for ecc memory expectation

  Scenario: VM with ECC expectation should start on a host with ECC memory
	Given hosts:
	  | address   | ram  | Cores | Threads | Architecture | Operating System | Distro | Distro Version |
	  | 127.0.0.2 | 6 GB | 2     | 4       | x86_64       | Linux            | CentOS | 7              |
	  | 127.0.0.3 | 6 GB | 2     | 4       | x86_64       | Linux            | CentOS | 7              |
	And 127.0.0.2 has ECC memory
	And 127.0.0.3 does not have ECC memory
	And VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
	And vm1 requires ECC memory
	When vm1 is started
	Then vm1 must be scheduled on 127.0.0.2

  Scenario: VM with no ECC expectation must be tossed to another host to start VM with ECC expectation1
	Given hosts:
	  | address   | ram  | Cores | Threads | Architecture | Operating System | Distro | Distro Version |
	  | 127.0.0.2 | 6 GB | 2     | 4       | x86_64       | Linux            | CentOS | 7              |
	  | 127.0.0.3 | 6 GB | 2     | 4       | x86_64       | Linux            | CentOS | 7              |
	And 127.0.0.2 has ECC memory
	And 127.0.0.3 does not have ECC memory
	And VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
	  | vm2  | 4 GB   | 4 GB   | 2    | x86_64       |
	And vm1 requires ECC memory
	And vm2 does not require ECC memory
	And vm2 is running on 127.0.0.2
	When vm1 is started
	Then vm2 must be migrated to 127.0.0.3 as step 1
	Then vm1 must be scheduled on 127.0.0.2 as step 2

