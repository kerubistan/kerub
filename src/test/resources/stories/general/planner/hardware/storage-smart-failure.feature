Feature: Action taken on physical storage device signaling failure

  Scenario: LVM PV fails - no workload, plenty of free capacity - we just drop it
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Distro Version |
	  | host-1.example.com | 2 GB | 2     | 4       | x86_64       | Linux            | CentOS Linux | 7.1            |
	And host host-1.example.com is Up
	And host host-1.example.com volume groups are:
	  | vg    | size | devices                                        |
	  | kerub | 3 TB | /dev/sda: 1 TB, /dev/sdb: 1 TB, /dev/sdc: 1 TB |
	When disk /dev/sda in host host-1.example.com signals failure
	Then disk /dev/sda in host host-1.example.com will be removed from VG kerub as step 1
