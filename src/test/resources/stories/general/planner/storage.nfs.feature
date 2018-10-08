Feature: NFS storage

  Scenario Outline: Share a disk with NFS
	Given hosts:
	  | address            | ram  | Cores | Threads | Architecture | Operating System | Distribution | Dist. Version |
	  | host-1.example.com | 2 GB | 2     | 4       | x86_64       | <OS>             | <Distro>     | <version>     |
	  | host-2.example.com | 8 GB | 2     | 4       | x86_64       | <OS>             | <Distro>     | <version>     |
	And Controller configuration 'nfs enabled' is enabled
	And host host-1.example.com filesystem is:
	  | mount point | size | x | filesystem |
	  | /kerub      | 4 TB | x | ext4       |
	And host host-1.example.com is Up
	And host host-2.example.com is Up
	And host host-1.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags   |
	  | GenuineIntel | 2400 | Intel Xeon | sse,etc |
	And host host-2.example.com CPUs are 4:
	  | Manufacturer | Mhz  | Name       | Flags       |
	  | GenuineIntel | 2400 | Intel Xeon | vmx,sse,etc |
	And software installed on host host-1.example.com:
	  | package              | version |
	  | qemu-kvm             | 2.4.1   |
	  | <libvirt-package>    | 1.2.18  |
	  | <nfs-server-package> | 1.0     |
	And software installed on host host-2.example.com:
	  | package           | version |
	  | qemu-kvm          | 2.4.1   |
	  | <libvirt-package> | 1.2.18  |
	And virtual storage devices:
	  | name          | size | ro    |
	  | system-disk-1 | 2 GB | false |
	And the virtual disk system-disk-1 is created on host-1.example.com at /kerub
	Given VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
	And system-disk-1 is attached to vm1
	When VM vm1 is started
	Then nfs must be started on host host-1.example.com as step 1
	And /kerub must be shared with nfs on host host-1.example.com as step 2
	And host-1.example.com:/kerub must be mounted on host-2.example.com as step 3
	And VM vm1 gets scheduled on host host-2.example.com as step 4


	Examples:
	  | OS    | Distro       | version | nfs-server-package | libvirt-package                |
	  | Linux | Fedora       | 23      | nfs-utils          | libvirt-client                 |
	  | Linux | openSUSE     | 13      | nfs-kernel-server  | libvirt-client                 |
	  | Linux | CentOS Linux | 7.1     | nfs-utils          | libvirt-client                 |
	  | Linux | Debian       | 8.6     | nfs-kernel-server  | libvirt-clients,libvirt-daemon |
	  | Linux | Ubuntu       | 14.0.4  | nfs-kernel-server  | libvirt-bin                    |
