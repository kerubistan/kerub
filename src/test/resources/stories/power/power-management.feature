Feature: Host power management

  Scenario Template: power off idle servers
	Given hosts:
	  | address            | ram | Cores | Threads | Architecture |  |
	  | host-1.example.com | 2GB | 2     | 4       | x86_64       |  |
	  | host-2.example.com | 2GB | 2     | 4       | x86_64       |  |
	Given VMs:
	  | name | MinRam | MaxRam | CPUs | Architecture |
	  | vm1  | 1GB    | 4GB    | 2    | x86_64       |
	And vm1 is running on host-1.example.com
	And host host-2.example.com has <type> power management
	When host-2.example.com is idle for 2 hours
	Then host-2.example.com will be powered down
	Examples: type
	  | type        |
	  | wake-on-lan |
	  | ipmi        |

  Scenario: do not power off a server if no power management
	Given hosts:
	  | address            | ram | Cores | Threads | Architecture |  |
	  | host-1.example.com | 2GB | 2     | 4       | x86_64       |  |
	And host host-1.example.com has no power management
	When host-2.example.com is idle for 2 hours
	Then host-2.example.com won't be powered down

