Feature: Storage migration

  Scenario: Dead migration of an LVM allocation triggered by host recycle
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Dist. Version |
	  | host-1.example.com | 2 GB | 2     | 4       | x86_64       | Linux            | CentOS Linux | 7             |
	  | host-2.example.com | 8 GB | 2     | 4       | x86_64       | Linux            | CentOS Linux | 7             |
	And Controller configuration 'lvm create volume enabled' is enabled
	And host host-1.example.com volume groups are:
	  | vg name | size   | pvs                                                                    |
	  | vg-1    | 512 GB | /dev/sda: 128 GB, /dev/sdb: 128 GB, /dev/sdc: 128 GB, /dev/sdd: 128 GB |
	And host host-2.example.com volume groups are:
	  | vg name | size   | pvs                                                                    |
	  | vg-1    | 512 GB | /dev/sda: 128 GB, /dev/sdb: 128 GB, /dev/sdc: 128 GB, /dev/sdd: 128 GB |
	And host host-1.example.com is Up
	And host host-2.example.com is Up
	And volume group vg-1 on host host-1.example.com has 500GB free capacity
	And volume group vg-1 on host host-2.example.com has 512GB free capacity
	And virtual storage devices:
	  | name          | size | ro    |
	  | system-disk-1 | 2 GB | false |
	And system-disk-1 is allocated on host host-1.example.com volume group vg-1
	And host host-1.example.com is scheduled for recycling
	When planner starts
	Then virtual disk system-disk-1 will be block dead-migrated from host-1.example.com to host-2.example.com
	And host host-1.example.com will be recycled