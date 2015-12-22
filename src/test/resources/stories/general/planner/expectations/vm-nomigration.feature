Feature: "No migration" expectation

  Scenario: vm without "No migration" expectation is preferred when selecting for migration
    Given hosts:
      | address           | ram  | Cores | Threads | Architecture |  |
      | host1.example.com | 6 GB | 2     | 4       | x86_64       |  |
      | host2.example.com | 6 GB | 2     | 4       | x86_64       |  |
    And VMs:
      | name | MinRam   | MaxRam   | CPUs | Architecture |
      | vm1  | 2.5 GB   | 2.5 GB   | 1    | x86_64       |
      | vm2  | 2.5 GB   | 2.5 GB   | 1    | x86_64       |

