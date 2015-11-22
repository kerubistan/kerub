Feature: Power-Save

  Scenario: A single unused host
    Given hosts:
      | address   | ram  | Cores | Threads | Architecture |  |
      | 127.0.0.5 | 6 GB | 2     | 4       | x86_64       |  |
    And no virtual machines
    And host 127.0.0.5 is Up
    When optimization is triggered
    Then host 127.0.0.1 should go to power-save

  Scenario: Two hosts, only one VM
    Given hosts:
      | address   | ram  | Cores | Threads | Architecture |  |
      | 127.0.0.2 | 6 GB | 2     | 4       | x86_64       |  |
      | 127.0.0.3 | 6 GB | 2     | 4       | x86_64       |  |
    Given VMs:
      | name | MinRam | MaxRam | CPUs | Architecture |
      | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
    And host 127.0.0.2 is Up
    And host 127.0.0.3 is Up
    And vm1 is running on 127.0.0.2
    When optimization is triggered
    Then host 127.0.0.1 should go to power-save
