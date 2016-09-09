Feature: storage management

  Scenario: The virtual disk must be created for the VM
	And hosts:
	  | address   | ram  | Cores | Threads | Architecture | Operating System |
	  | 127.0.0.5 | 6 GB | 2     | 4       | x86_64       | Linux            |
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
	And software installed on host 127.0.0.5:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And host 127.0.0.5 is Up
	And system-disk-1 is attached to vm1
	And system-disk-1 is not yet created
	When VM vm1 is started
	Then the virtual disk system-disk-1 must be allocated on 127.0.0.5 under /var
	And VM vm1 gets scheduled on host 127.0.0.5 as step 2

  Scenario: The virtual disk must be created for the VM, only LVM is available
	Given hosts:
	  | address   | ram  | Cores | Threads | Architecture | Operating System |
	  | 127.0.0.5 | 6 GB | 2     | 4       | x86_64       | Linux            |
	And host 127.0.0.5 filesystem is:
	  | mount point | size  | free |
	  | /           | 10 GB | 2 GB |
	  | /tmp        | 8 GB  | 4 GB |
	And host 127.0.0.5 volume groups are:
	  | vg name    | size   | pvs                            |
	  | volgroup-1 | 512 GB | 128 GB, 128 GB, 128 GB, 128 GB |
	And software installed on host 127.0.0.5:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
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
	And VM vm1 gets scheduled on host 127.0.0.5 as step 2

  Scenario: disk created for the availability requirement on LVM
	Given hosts:
	  | address   | ram  | Cores | Threads | Architecture | Operating System |
	  | 127.0.0.5 | 6 GB | 2     | 4       | x86_64       | Linux            |
	And host 127.0.0.5 volume groups are:
	  | vg name    | size   | pvs                            |
	  | volgroup-1 | 512 GB | 128 GB, 128 GB, 128 GB, 128 GB |
	And host 127.0.0.5 is Up
	And volume group volgroup-1 on host 127.0.0.5 has 500GB free capacity
	And virtual storage devices:
	  | name          | size | ro    |
	  | system-disk-1 | 2 GB | false |
	When virtual disk system-disk-1 gets an availability expectation
	Then the virtual disk system-disk-1 must be allocated on 127.0.0.5 under on the volume group volgroup-1

  Scenario: disk created for the availability requirement on gvinum
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distro  | Distro Version |
	  | host-1.example.com | 6 GB | 2     | 4       | x86_64       | BSD              | FreeBSD | 7.1            |
	And host host-1.example.com gvinum disks are:
	  | disk name | device | size   |
	  | disk-1    | ada0   | 512 GB |
	And host host-1.example.com is Up
	And gvinum disk disk-1 on host host-1.example.com has 500GB free capacity
	And virtual storage devices:
	  | name          | size | ro    |
	  | system-disk-1 | 2 GB | false |
	When virtual disk system-disk-1 gets an availability expectation
	Then the virtual disk system-disk-1 must be allocated on host-1.example.com under on the gvinum disk disk-1

  Scenario: disk created for the availability requirement on gvinum on the disk that has enough capacity
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distro  | Distro version |
	  | host-1.example.com | 6 GB | 2     | 4       | x86_64       | BSD              | FreeBSD | 7.1            |
	And host host-1.example.com gvinum disks are:
	  | disk name | device | size   |
	  | disk-1    | ada0   | 512 GB |
	  | disk-2    | ada1   | 512 GB |
	And host host-1.example.com is Up
	And gvinum disk disk-1 on host host-1.example.com has 500GB free capacity
	And gvinum disk disk-2 on host host-1.example.com has 1GB free capacity
	And virtual storage devices:
	  | name          | size | ro    |
	  | system-disk-1 | 2 GB | false |
	When virtual disk system-disk-1 gets an availability expectation
	Then the virtual disk system-disk-1 must be allocated on host-1.example.com under on the gvinum disk disk-1

  Scenario: The only host needs to be waken up to make the disk available
	Given hosts:
	  | address   | ram  | Cores | Threads | Architecture | Operating System |
	  | 127.0.0.5 | 6 GB | 2     | 4       | x86_64       | Linux            |
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


  Scenario: Share an existing disk with ISCSI to start the VM
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Dist. Version |
	  | host-1.example.com | 2 GB | 2     | 4       | x86_64       | Linux            | Fedora       | 23            |
	  | host-2.example.com | 8 GB | 2     | 4       | x86_64       | Linux            | Fedora       | 23            |
	And host host-1.example.com volume groups are:
	  | vg name | size   | pvs                            |
	  | vg-1    | 512 GB | 128 GB, 128 GB, 128 GB, 128 GB |
	And host host-1.example.com is Up
	And host host-2.example.com is Up
	And software installed on host host-2.example.com:
	  | package           | version |
	  | qemu-kvm          | 2.4.1   |
	  | libvirt           | 1.2.18  |
	  | scsi-target-utils | 1.0     |
	And software installed on host host-1.example.com:
	  | package  | version |
	  | qemu-kvm | 2.4.1   |
	  | libvirt  | 1.2.18  |
	And virtual storage devices:
	  | name          | size | ro    |
	  | system-disk-1 | 2 GB | false |
	And system-disk-1 is allocated on host host-1.example.com volume group vg-1
	Given VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
	And system-disk-1 is attached to vm1
	When VM vm1 is started
	Then system-disk-1 must be shared with iscsi on host host-1.example.com as step 1
	And VM vm1 gets scheduled on host host-2.example.com as step 2

  Scenario: Disk already shared with iscsi
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Distro Version |
	  | host-1.example.com | 2 GB | 2     | 4       | x86_64       | Linux            | Fedora       | 23             |
	  | host-2.example.com | 8 GB | 2     | 4       | x86_64       | Linux            | Fedora       | 23             |
	And host 127.0.0.5 volume groups are:
	  | vg name | size   | pvs                            |
	  | vg-1    | 512 GB | 128 GB, 128 GB, 128 GB, 128 GB |
	And host host-1.example.com is Up
	And host host-2.example.com is Up
	And software installed on host host-1.example.com:
	  | package           | version |
	  | qemu-kvm          | 2.4.1   |
	  | libvirt           | 1.2.18  |
	  | libvirt           | 1.2.18  |
	  | scsi-target-utils | 1.0.5   |
	And software installed on host host-2.example.com:
	  | package           | version |
	  | qemu-kvm          | 2.4.1   |
	  | libvirt           | 1.2.18  |
	  | scsi-target-utils | 1.0.5   |
	And virtual storage devices:
	  | name          | size | ro    |
	  | system-disk-1 | 2 GB | false |
	And system-disk-1 is allocated on host host-1.example.com volume group vg-1
	And system-disk-1 is shared with iscsi on host host-1.example.com
	Given VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
	And system-disk-1 is attached to vm1
	When VM vm1 is started
	Then VM vm1 gets scheduled on host host-2.example.com as step 1


