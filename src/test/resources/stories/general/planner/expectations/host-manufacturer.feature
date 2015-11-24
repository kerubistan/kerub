Feature: support for host manufacturer expectation

  Scenario: host selection with manufacturer info
    Given hosts:
      | address           | ram  | Cores | Threads | Architecture |  |
      | host1.example.com | 6 GB | 2     | 4       | x86_64       |  |
      | host2.example.com | 6 GB | 2     | 4       | x86_64       |  |
    And host1.example.com manufaturer is awesomehardware
    And host2.example.com manufaturer is enterprisejunkyard
    And host host1.example.com is Up
    And host host2.example.com is Up
    And VMs:
      | name | MinRam | MaxRam | CPUs | Architecture |
      | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
    And VM vm1 requires manufacturer enterprisejunkyard
    When VM vm1 is started
    Then VM vm1 gets scheduled on host host2.example.com