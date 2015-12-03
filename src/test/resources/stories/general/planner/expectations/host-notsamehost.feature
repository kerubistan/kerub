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
    When VM vm1 is started
    Then VM vm1 gets scheduled on host host1.example.com

  Scenario: the other vm must be migrated to another host to be able to start
    Given hosts:
      | address            | ram    | Cores | Threads | Architecture |  |
      | host-s.example.com | 2 GB   | 2     | 4       | x86_64       |  |
      | host-b.example.com | 4.5 GB | 2     | 4       | x86_64       |  |
    And VMs:
      | name  | MinRam | MaxRam | CPUs | Architecture |
      | vm-s  | 1 GB   | 1 GB   | 2    | x86_64       |
      | vm-b  | 4 GB   | 4 GB   | 2    | x86_64       |
    And host host-s.example.com is Up
    And host host-b.example.com is Up
    And vm-s is running on host-b.example.com
    And vm-s has notsame host expectation against vm-b
    When VM vm-b is started
    Then vm-s will be migrated to host-s.example.com as step 1
    And VM vm-b gets scheduled on host host-b.example.com as step 2

