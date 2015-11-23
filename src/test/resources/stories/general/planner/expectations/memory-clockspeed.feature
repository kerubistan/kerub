Feature: support for memory clock speed expectation

  Scenario: Start a virtual machine with notsame expectation on it
    Given hosts:
      | address           | ram  | Cores | Threads | Architecture |  |
      | host1.example.com | 6 GB | 2     | 4       | x86_64       |  |
      | host2.example.com | 6 GB | 2     | 4       | x86_64       |  |
    And VMs:
      | name | MinRam | MaxRam | CPUs | Architecture |
      | vm1  | 1 GB   | 1 GB   | 2    | x86_64       |
    And vm1 has clock speed expectation 1700 Mhz
    And host host1.example.com is Up
    And host host2.example.com is Up
    And host1.example.com memory clockspeed is 1300 Mhz
    And host2.example.com memory clockspeed is 2300 Mhz
    When vm1 is started
    Then vm1 must be scheduled on host2.example.com
