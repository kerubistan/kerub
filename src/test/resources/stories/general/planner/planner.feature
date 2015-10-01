Feature: planner and optimizer

  Scenario: Start a VM with single host
    Given VMs:
      | name | MinRam | MaxRam | CPUs | Architecture |
      | vm1  | 1GB    | 4GB     | 2    | x86_64       |
    And hosts:
      | address   | ram | Cores | Threads | Architecture |  |
      | 127.0.0.5 | 2GB | 2     | 4       | x86_64       |  |
    And host 127.0.0.5 is Up
    When VM vm1 is started
    Then VM vm1 gets scheduled on host 127.0.0.5

  Scenario: Start a VM with two hosts, one of them is does not match required architecture
    Given VMs:
      | name | MinRam | MaxRam | CPUs | Architecture |
      | vm1  | 1GB     | 2GB    | 2    | x86_64       |
    And hosts:
      | address   | ram | Cores | Threads | Architecture |  |
      | 127.0.0.5 | 2GB | 2     | 4       | x86_64       |  |
      | 127.0.0.6 | 2GB | 2     | 2       | ARM          |  |
    And host 127.0.0.5 is Up
    And host 127.0.0.6 is Up
    When VM vm1 is started
    Then VM vm1 gets scheduled on host 127.0.0.5

  Scenario: Start a VM with two hosts, one of them is does not match required amount of cores
    Given VMs:
      | name | MinRam | MaxRam | CPUs | Architecture |
      | vm1  | 1 GB   | 2 GB   | 2    | x86_64       |
    And hosts:
      | address   | ram  | Cores | Threads | Architecture |  |
      | 127.0.0.5 | 2 GB | 2     | 4       | x86_64       |  |
      | 127.0.0.6 | 2 GB | 1     | 2       | x86_64       |  |
    And host 127.0.0.5 is Up
    And host 127.0.0.6 is Up
    When VM vm1 is started
    Then VM vm1 gets scheduled on host 127.0.0.5

  Scenario: Start a VM with two hosts, one of them is does not match required amount of RAM
    Given VMs:
      | name | MinRam | MaxRam | CPUs | Architecture |
      | vm1  | 1 GB   | 2 GB   | 2    | x86_64       |
    And hosts:
      | address   | ram  | Cores | Threads | Architecture |  |
      | 127.0.0.5 | 2 GB | 2     | 4       | x86_64       |  |
      | 127.0.0.6 | 1 GB | 2     | 4       | x86_64       |  |
    And host 127.0.0.5 is Up
    And host 127.0.0.6 is Up
    When VM vm1 is started
    Then VM vm1 gets scheduled on host 127.0.0.5

