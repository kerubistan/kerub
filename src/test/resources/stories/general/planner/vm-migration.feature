Feature: VM migrations

  Scenario Outline: Migrate the VMs from host to be recycled to another host
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Dist. Version |
	  | host-1.example.com | 8 GB | 2     | 4       | x86_64       | <OS>             | <Distro>     | <version>     |
	  | host-2.example.com | 8 GB | 2     | 4       | x86_64       | <OS>             | <Distro>     | <version>     |
	And VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
	And host host-1.example.com is Up
	And host host-1.example.com is scheduled for recycling
	And host host-2.example.com is Up
	And vm1 is running on host-1.example.com
	When planner starts
	Then vm1 will be migrated to host-2.example.com as step 1
	And host host-1.example.com will be powered down
	And host host-1.example.com will be recycled

	Examples:
	  | OS    | Distro | version |
	  | Linux | CentOs | 7.3     |