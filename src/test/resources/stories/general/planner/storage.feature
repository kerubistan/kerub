Feature: storage management


  Scenario Outline: Share an existing disk with ISCSI to start the VM
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Dist. Version |
	  | host-1.example.com | 2 GB | 2     | 4       | x86_64       | <OS>             | <Distro>     | <version>     |
	  | host-2.example.com | 8 GB | 2     | 4       | x86_64       | <OS>             | <Distro>     | <version>     |
	And Controller configuration 'lvm create volume enabled' is enabled
	And host host-1.example.com volume groups are:
	  | vg name | size   | pvs                                                                    |
	  | vg-1    | 512 GB | /dev/sda: 128 GB, /dev/sdb: 128 GB, /dev/sdc: 128 GB, /dev/sdd: 128 GB |
	And host host-1.example.com is Up
	And host host-2.example.com is Up
	And host host-1.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags       |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc |
	And host host-2.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags       |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc |
	And software installed on host host-2.example.com:
	  | package           | version |
	  | qemu-kvm          | 2.4.1   |
	  | <libvirt-package> | 1.2.18  |
	And software installed on host host-1.example.com:
	  | package                | version |
	  | qemu-kvm               | 2.4.1   |
	  | <libvirt-package>      | 1.2.18  |
	  | <iscsi-server-package> | 1.0     |
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

	Examples:
	  | OS    | Distro       | version | iscsi-server-package | libvirt-package                |
	  | Linux | Fedora       | 23      | scsi-target-utils    | libvirt-client                 |
	  | Linux | openSUSE     | 13      | tgt                  | libvirt-client                 |
	  | Linux | CentOS Linux | 7.1     | scsi-target-utils    | libvirt-client                 |
	  | Linux | Debian       | 8.6     | tgt                  | libvirt-clients,libvirt-daemon |
	  | Linux | Ubuntu       | 14.0.4  | tgt                  | libvirt-bin                    |

  Scenario Outline: Create a large disk on multiple gvinum partitions
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Dist. Version |
	  | host-1.example.com | 2 GB | 2     | 4       | x86_64       | BSD              | FreeBSD      | <version>     |
	And Controller configuration 'gvinum create volume enabled' is enabled
	And host host-1.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags       |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc |
	And host host-1.example.com gvinum disks are:
	  | name  | device     | size          |
	  | disk1 | /dev/disk1 | <disk-1-size> |
	  | disk2 | /dev/disk2 | <disk-2-size> |
	  | disk3 | /dev/disk3 | <disk-3-size> |
	  | disk4 | /dev/disk4 | <disk-4-size> |
	And host host-1.example.com is Up
	And gvinum disk disk1 on host host-1.example.com has <disk-1-free> free capacity
	And gvinum disk disk2 on host host-1.example.com has <disk-2-free> free capacity
	And gvinum disk disk3 on host host-1.example.com has <disk-3-free> free capacity
	And gvinum disk disk4 on host host-1.example.com has <disk-4-free> free capacity
	And virtual storage devices:
	  | name          | size         | ro    |
	  | system-disk-1 | <vdisk-size> | false |
	When virtual disk system-disk-1 gets an availability expectation
	Then the virtual disk system-disk-1 must be allocated on host-1.example.com under on the gvinum disks: <disks-csv>

	Examples:
	  | version | vdisk-size | disk-1-size | disk-1-free | disk-2-size | disk-2-free | disk-3-size | disk-3-free | disk-4-size | disk-4-free | disks-csv               |
	  | 10      | 7GB        | 2GB         | 2GB         | 2GB         | 2GB         | 2GB         | 2GB         | 2GB         | 2GB         | disk1,disk2,disk3,disk4 |
	  | 10      | 2GB        | 2GB         | 600MB       | 2GB         | 600MB       | 2GB         | 600MB       | 2GB         | 600MB       | disk1,disk2,disk3,disk4 |
	  | 10      | 2GB        | 2GB         | 700MB       | 2GB         | 700MB       | 2GB         | 700MB       | 2GB         | 0MB         | disk1,disk2,disk3       |
	  | 10      | 2GB        | 2GB         | 0MB         | 2GB         | 800MB       | 2GB         | 800MB       | 2GB         | 800MB       | disk2,disk3,disk4       |
	  | 10      | 2GB        | 2GB         | 0MB         | 2GB         | 0MB         | 2GB         | 1500MB      | 2GB         | 1500MB      | disk2,disk3,disk4       |

  # In this story, host-1 can allocate the storage on only two disks, while host-2 needs at least 3
  # since with the number of disks the risk of data loss rises, the allocation on host-1 is more favorable
  Scenario: Creating concatenated disk on gvinum disks - less disks is better then more in the concatenated config
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Dist. Version |
	  | host-1.example.com | 2 GB | 2     | 4       | x86_64       | BSD              | FreeBSD      | 10            |
	  | host-2.example.com | 2 GB | 2     | 4       | x86_64       | BSD              | FreeBSD      | 10            |
	And Controller configuration 'gvinum create volume enabled' is enabled
	And host host-1.example.com gvinum disks are:
	  | name  | device     | size |
	  | disk1 | /dev/disk1 | 2GB  |
	  | disk2 | /dev/disk2 | 2GB  |
	  | disk3 | /dev/disk3 | 2GB  |
	  | disk4 | /dev/disk4 | 2GB  |
	And host host-2.example.com gvinum disks are:
	  | name  | device     | size |
	  | disk1 | /dev/disk1 | 2GB  |
	  | disk2 | /dev/disk2 | 2GB  |
	  | disk3 | /dev/disk3 | 2GB  |
	  | disk4 | /dev/disk4 | 2GB  |
	And host host-1.example.com is Up
	And host host-2.example.com is Up
	And gvinum disk disk1 on host host-1.example.com has 2GB free capacity
	And gvinum disk disk2 on host host-1.example.com has 2GB free capacity
	And gvinum disk disk3 on host host-1.example.com has 0GB free capacity
	And gvinum disk disk4 on host host-1.example.com has 0GB free capacity
	And gvinum disk disk1 on host host-2.example.com has 1GB free capacity
	And gvinum disk disk2 on host host-2.example.com has 1GB free capacity
	And gvinum disk disk3 on host host-2.example.com has 1GB free capacity
	And gvinum disk disk4 on host host-2.example.com has 1GB free capacity
	And virtual storage devices:
	  | name          | size | ro    |
	  | system-disk-1 | 3GB  | false |
	When virtual disk system-disk-1 gets an availability expectation
	Then the virtual disk system-disk-1 must be allocated on host-1.example.com under on the gvinum disks: disk1,disk2


  Scenario Outline: Share an existing disk with ISCSI on a BSD to start the VM on Linux
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Dist. Version |
	  | host-1.example.com | 2 GB | 2     | 4       | x86_64       | <OS-1>           | <Distro-1>   | <version-1>   |
	  | host-2.example.com | 8 GB | 2     | 4       | x86_64       | <OS-2>           | <Distro-2>   | <version-2>   |
	And Controller configuration 'gvinum create volume enabled' is enabled
	And host host-1.example.com gvinum disks are:
	  | name | device    | size |
	  | test | /dev/test | 1 TB |
	And host host-1.example.com is Up
	And host host-2.example.com is Up
	And host host-1.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags       |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc |
	And host host-2.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags       |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc |
	And software installed on host host-2.example.com:
	  | package                | version |
	  | qemu-kvm               | 2.4.1   |
	  | <libvirt-package>      | 1.2.18  |
	  | <iscsi-server-package> | 1.0     |
	And software installed on host host-1.example.com:
	  | package           | version |
	  | qemu-kvm          | 2.4.1   |
	  | <libvirt-package> | 1.2.18  |
	And virtual storage devices:
	  | name          | size | ro    |
	  | system-disk-1 | 2 GB | false |
	And virtual storage system-disk-1 allocated on host host-1.example.com using simple gvinum disk name test
	Given VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
	And system-disk-1 is attached to vm1
	When VM vm1 is started
	Then system-disk-1 must be shared with iscsi on host host-1.example.com as step 1
	And VM vm1 gets scheduled on host host-2.example.com as step 2

	Examples:
	  | OS-1 | Distro-1 | version-1 | iscsi-server-package | OS-2  | Distro-2     | version-2 | libvirt-package |
	  | BSD  | FreeBSD  | 10        | -                    | Linux | CentOS Linux | 7.1       | libvirt-client  |
	  | BSD  | FreeBSD  | 10        | -                    | Linux | Fedora       | 22        | libvirt-client  |
	  | BSD  | FreeBSD  | 10        | -                    | Linux | openSUSE     | 13        | libvirt-client  |


  Scenario Outline: Disk already shared with iscsi
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Distro Version |
	  | host-1.example.com | 2 GB | 2     | 4       | x86_64       | <OS>             | <Distro>     | <version>      |
	  | host-2.example.com | 8 GB | 2     | 4       | x86_64       | <OS>             | <Distro>     | <version>      |
	And host host-1.example.com volume groups are:
	  | vg name | size   | pvs                                                                    |
	  | vg-1    | 512 GB | /dev/sda: 128 GB, /dev/sdb: 128 GB, /dev/sdc: 128 GB, /dev/sdd: 128 GB |
	And Controller configuration 'lvm create volume enabled' is enabled
	And host host-1.example.com is Up
	And host host-2.example.com is Up
	And host host-1.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags       |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc |
	And host host-2.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags       |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc |
	And software installed on host host-1.example.com:
	  | package                | version |
	  | qemu-kvm               | 2.4.1   |
	  | <libvirt-package>      | 1.2.18  |
	  | <iscsi-server-package> | 1.0.5   |
	And software installed on host host-2.example.com:
	  | package                | version |
	  | qemu-kvm               | 2.4.1   |
	  | <libvirt-package>      | 1.2.18  |
	  | <iscsi-server-package> | 1.0.5   |
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

	Examples:
	  | OS    | Distro       | version | iscsi-server-package | libvirt-package |
	  | Linux | Fedora       | 23      | scsi-target-utils    | libvirt-client  |
	  | Linux | openSUSE     | 13      | tgt                  | libvirt-client  |
	  | Linux | CentOS Linux | 7.1     | scsi-target-utils    | libvirt-client  |

  Scenario: Unallocated disk removed
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Distro Version |
	  | host-1.example.com | 2 GB | 2     | 4       | x86_64       | Linux            | CentOS Linux | 7,1            |
	And virtual storage devices:
	  | name        | size | ro    |
	  | test-disk-1 | 2 GB | false |
	When disk test-disk-1 is recycled
	Then disk test-disk-1 will be deleted as step 1

  Scenario: Disk allocated on fs mount point /kerub removed
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Distro Version |
	  | host-1.example.com | 2 GB | 2     | 4       | x86_64       | Linux            | CentOS Linux | 7.1            |
	And host host-1.example.com filesystem is:
	  | mount  | size | - | type |
	  | /kerub | 1 TB |   | ext4 |
	And host host-1.example.com is Up
	And virtual storage devices:
	  | name        | size | ro    |
	  | test-disk-1 | 2 GB | false |
	And virtual storage test-disk-1 allocated on host host-1.example.com using fs mount point /kerub
	When disk test-disk-1 is recycled
	Then disk test-disk-1 will be unallocated as step 1
	And disk test-disk-1 will be deleted as step 2

  Scenario: Disk allocated on lvm volume group kerub
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Distro Version |
	  | host-1.example.com | 2 GB | 2     | 4       | x86_64       | Linux            | CentOS Linux | 7.1            |
	And host host-1.example.com volume groups are:
	  | vg    | size | devices        |
	  | kerub | 1 TB | /dev/sda: 1 TB |
	And host host-1.example.com is Up
	And virtual storage devices:
	  | name        | size | ro    |
	  | test-disk-1 | 2 GB | false |
	And virtual storage test-disk-1 allocated on host host-1.example.com using lvm volume group kerub
	When disk test-disk-1 is recycled
	Then disk test-disk-1 will be unallocated as step 1
	And disk test-disk-1 will be deleted as step 2

  Scenario: Disk allocated on simple gvinum disk id 5e5bf833-d54a-4732-b46b-7a987f905723
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Distro Version |
	  | host-1.example.com | 2 GB | 2     | 4       | x86_64       | BSD              | FreeBSD      | 1.1            |
	And host host-1.example.com gvinum disks are:
	  | device | name | size |
	  | sda1   | sda1 | 1 TB |
	And host host-1.example.com is Up
	And virtual storage devices:
	  | name        | size | ro    |
	  | test-disk-1 | 2 GB | false |
	And virtual storage test-disk-1 allocated on host host-1.example.com using simple gvinum disk name sda1
	When disk test-disk-1 is recycled
	Then disk test-disk-1 will be unallocated as step 1
	And disk test-disk-1 will be deleted as step 2

  Scenario: LVM Thin Provisioning
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Distro Version |
	  | host-1.example.com | 2 GB | 2     | 4       | x86_64       | Linux            | CentOS Linux | 7.1            |
	And host host-1.example.com volume groups are:
	  | vg name | size   | pvs              |
	  | vg-1    | 500 GB | /dev/sda: 500 GB |
	And virtual storage devices:
	  | name        | size | ro    |
	  | test-disk-1 | 1 TB | false |
	And Controller configuration 'lvm create volume enabled' is enabled
	And host host-1.example.com is Up
	And volume group vg-1 on host host-1.example.com has 200GB free capacity
	When virtual disk test-disk-1 gets an availability expectation
	Then the virtual disk test-disk-1 must be thin-allocated on host-1.example.com under on the volume group vg-1

  Scenario: LVM and storage redundancy
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Distro Version |
	  | host-1.example.com | 2 GB | 2     | 4       | x86_64       | Linux            | CentOS Linux | 7.1            |
	  | host-2.example.com | 2 GB | 2     | 4       | x86_64       | Linux            | CentOS Linux | 7.1            |
	And host host-2.example.com volume groups are:
	  | vg name | size   | pvs                                |
	  | vg-2    | 500 GB | /dev/sda: 512 GB, /dev/sdb: 512 GB |
	And host host-1.example.com volume groups are:
	  | vg name | size   | pvs               |
	  | vg-1    | 500 GB | /dev/sda: 1024 GB |
	And virtual storage devices:
	  | name        | size   | ro    |
	  | test-disk-1 | 300 GB | false |
	And Controller configuration 'lvm create volume enabled' is enabled
	And host host-1.example.com is Up
	And volume group vg-1 on host host-1.example.com has 800GB free capacity
	And host host-2.example.com is Up
	And volume group vg-2 on host host-2.example.com has 800GB free capacity
	And test-disk-1 has storage redundancy expectation: 1 copies
	When virtual disk test-disk-1 gets an availability expectation
	Then the virtual disk test-disk-1 must be allocated on host-2.example.com under on the volume group vg-2
	And the virtual disk test-disk-1 must be mirrored using lvm - 1 mirrors as step 2
