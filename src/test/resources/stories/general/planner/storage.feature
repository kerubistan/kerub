Feature: storage management

  Scenario: The virtual disk must be created for the VM
	And hosts:
	  | address   | ram  | Cores | Threads | Architecture |  |
	  | 127.0.0.5 | 6 GB | 2     | 4       | x86_64       |  |
	And host 127.0.0.5 filesystem is:
	  | mount point | size   | free   |
	  | /var        | 128 GB | 128 GB |
	  | /           | 10 GB  | 2 GB   |
	  | /tmp        | 8 GB   | 4 GB   |
	Given VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
	And virtual storage devices:
	  | name          | size | ro    |
	  | system-disk-1 | 2 GB | false |
	And host 127.0.0.5 is Up
	And system-disk-1 is attached to vm1
	And system-disk-1 is not yet created
	When VM vm1 is started
	Then the virtual disk system-disk-1 must be allocated on 127.0.0.5 under /var
	And VM vm1 gets scheduled on host 127.0.0.5

  Scenario: The virtual disk must be created for the VM, only LVM is available
	Given hosts:
	  | address   | ram  | Cores | Threads | Architecture |  |
	  | 127.0.0.5 | 6 GB | 2     | 4       | x86_64       |  |
	And host 127.0.0.5 filesystem is:
	  | mount point | size  | free |
	  | /           | 10 GB | 2 GB |
	  | /tmp        | 8 GB  | 4 GB |
	And host 127.0.0.5 volume groups are:
	  | vg name    | size   | pvs                            |
	  | volgroup-1 | 512 GB | 128 GB, 128 GB, 128 GB, 128 GB |
	Given VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
	And virtual storage devices:
	  | name          | size | ro    |
	  | system-disk-1 | 2 GB | false |
	And host 127.0.0.5 is Up
	And volume group volgroup-1 on host 127.0.0.5 has 500GB free capacity
	And system-disk-1 is attached to vm1
	And system-disk-1 is not yet created
	When VM vm1 is started
	Then the virtual disk system-disk-1 must be allocated on 127.0.0.5 under on the volume group volgroup-1
	And VM vm1 gets scheduled on host 127.0.0.5

  Scenario: disk created for the availability requirement
	Given hosts:
	  | address   | ram  | Cores | Threads | Architecture |  |
	  | 127.0.0.5 | 6 GB | 2     | 4       | x86_64       |  |
	And host 127.0.0.5 volume groups are:
	  | vg name    | size   | pvs                            |
	  | volgroup-1 | 512 GB | 128 GB, 128 GB, 128 GB, 128 GB |
	And host 127.0.0.5 is Up
	And virtual storage devices:
	  | name          | size | ro    |
	  | system-disk-1 | 2 GB | false |
	When virtual disk system-disk-1 gets an availability expectation
	Then the virtual disk system-disk-1 must be allocated on 127.0.0.5 under on the volume group volgroup-1

  Scenario: The only host needs to be waken up to make the disk available
	Given hosts:
	  | address   | ram  | Cores | Threads | Architecture |  |
	  | 127.0.0.5 | 6 GB | 2     | 4       | x86_64       |  |
	And host 127.0.0.5 volume groups are:
	  | vg name    | size   | pvs                            |
	  | volgroup-1 | 512 GB | 128 GB, 128 GB, 128 GB, 128 GB |
	And host 127.0.0.5 is Down
	And virtual storage devices:
	  | name          | size | ro    |
	  | system-disk-1 | 2 GB | false |
	When virtual disk system-disk-1 gets an availability expectation
	Then host 127.0.0.5 must be waken up as step 1
	And the virtual disk system-disk-1 must be allocated on 127.0.0.5 under on the volume group volgroup-1 as step 2
