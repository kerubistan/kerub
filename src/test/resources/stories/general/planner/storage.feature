Feature: storage management


  Scenario Outline: Share an existing disk with ISCSI to start the VM
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Dist. Version |
	  | host-1.example.com | 2 GB | 2     | 4       | x86_64       | <OS>             | <Distro>     | <version>     |
	  | host-2.example.com | 8 GB | 2     | 4       | x86_64       | <OS>             | <Distro>     | <version>     |
	And host host-1.example.com volume groups are:
	  | vg name | size   | pvs                            |
	  | vg-1    | 512 GB | 128 GB, 128 GB, 128 GB, 128 GB |
	And host host-1.example.com is Up
	And host host-2.example.com is Up
	And software installed on host host-2.example.com:
	  | package                | version |
	  | qemu-kvm               | 2.4.1   |
	  | libvirt                | 1.2.18  |
	  | <iscsi-server-package> | 1.0     |
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

	Examples:
	  | OS    | Distro           | version | iscsi-server-package |
	  | Linux | Fedora           | 23      | scsi-target-utils    |
	  | Linux | openSUSE         | 13      | tgt                  |
	  | Linux | Centos Linux     | 7.1     | scsi-target-utils    |
	  | Linux | Debian GNU/Linux | 8.6     | tgt                  |
	  | Linux | Ubuntu           | 14.0.4  | tgt                  |

  Scenario Outline: Share an existing disk with ISCSI on a BSD to start the VM on Linux
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Dist. Version |
	  | host-1.example.com | 2 GB | 2     | 4       | x86_64       | <OS-1>           | <Distro-1>   | <version-1>   |
	  | host-2.example.com | 8 GB | 2     | 4       | x86_64       | <OS-2>           | <Distro-2>   | <version-2>   |
	And host host-1.example.com gvinum disks are:
	  | name | device    | size |
	  | test | /dev/test | 1 TB |
	And host host-1.example.com is Up
	And host host-2.example.com is Up
	And software installed on host host-2.example.com:
	  | package                | version |
	  | qemu-kvm               | 2.4.1   |
	  | libvirt                | 1.2.18  |
	  | <iscsi-server-package> | 1.0     |
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

	Examples:
	  | OS-1 | Distro-1 | version-1 | iscsi-server-package | OS-2  | Distro-2     | version-2 |
	  | BSD  | FreeBSD  | 10        | -                    | Linux | Centos Linux | 7.1       |
	  | BSD  | FreeBSD  | 10        | -                    | Linux | Fedora       | 22        |
	  | BSD  | FreeBSD  | 10        | -                    | Linux | openSUSE     | 13        |


  Scenario Outline: Disk already shared with iscsi
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Distro Version |
	  | host-1.example.com | 2 GB | 2     | 4       | x86_64       | <OS>             | <Distro>     | <version>      |
	  | host-2.example.com | 8 GB | 2     | 4       | x86_64       | <OS>             | <Distro>     | <version>      |
	And host 127.0.0.5 volume groups are:
	  | vg name | size   | pvs                            |
	  | vg-1    | 512 GB | 128 GB, 128 GB, 128 GB, 128 GB |
	And host host-1.example.com is Up
	And host host-2.example.com is Up
	And software installed on host host-1.example.com:
	  | package                | version |
	  | qemu-kvm               | 2.4.1   |
	  | libvirt                | 1.2.18  |
	  | libvirt                | 1.2.18  |
	  | <iscsi-server-package> | 1.0.5   |
	And software installed on host host-2.example.com:
	  | package                | version |
	  | qemu-kvm               | 2.4.1   |
	  | libvirt                | 1.2.18  |
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
	  | OS    | Distro       | version | iscsi-server-package |
	  | Linux | Fedora       | 23      | scsi-target-utils    |
	  | Linux | openSUSE     | 13      | tgt                  |
	  | Linux | CentOS Linux | 7.1     | scsi-target-utils    |
