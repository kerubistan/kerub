Feature: support not-same-host expectation

  Scenario: Start a virtual machine with notsame expectation on it
    Given hosts:
      | address           | ram  | Cores | Threads | Architecture |  |
      | host1.example.com | 6 GB | 2     | 4       | x86_64       |  |
      | host2.example.com | 6 GB | 2     | 4       | x86_64       |  |
    And VMs:
      | name | MinRam | MaxRam | CPUs | Architecture |
      | vm1  | 1 GB   | 1 GB   | 2    | x86_64       |
      | vm2  | 1 GB   | 1 GB   | 2    | x86_64       |
    And host host1.example.com is Up
    And host host2.example.com is Up
    And vm2 is running on host2.example.com
    And vm1 has notsame host expectation against vm2
    When vm1 is started
    Then vm1 must bs scheduled on host1.example.com

  Scenario: the other vm must be migrated to another host to be able to start
    Given hosts:
      | address           | ram  | Cores | Threads | Architecture |  |
      | host1.example.com | 2 GB | 2     | 4       | x86_64       |  |
      | host2.example.com | 8 GB | 2     | 4       | x86_64       |  |
    And VMs:
      | name | MinRam | MaxRam | CPUs | Architecture |
      | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
      | vm2  | 1 GB   | 1 GB   | 2    | x86_64       |
    And host host1.example.com is Up
    And host host2.example.com is Up
    And vm2 is running on host2.example.com
    And vm1 has notsame host expectation against vm2
    When vm1 is started
    Then vm2 must be migrated to host1.example.com
    And vm1 must be scheduled on host2.example.com

