Feature: Scheduler and optimizer

  Scenario: Start a VM with single host
    Given a VM:
      | name | MinRam | MaxRam | CPUs | Architecture |
      | vm1  | 1G     | 4G     | 2    | x86_64       |
    And hosts:
      | address   | ram | Cores | Threads | Architecture |  |
      | 127.0.0.5 | 2G  | 2     | 4       | X86_64       |  |
    When VM vm1 is started
    Then VM vm1 gets scheduled on host 127.0.0.5

  Scenario: Start a VM with two hosts, one of them is does not match required architecture
    Given a VM:
      | name | MinRam | MaxRam | CPUs | Architecture |
      | vm1  | 1G     | 2G     | 2    | x86_64       |
    And hosts:
      | address   | ram | Cores | Threads | Architecture |  |
      | 127.0.0.5 | 2G  | 2     | 4       | X86_64       |  |
      | 127.0.0.6 | 2G  | 2     | 2       | ARM          |  |
    When VM vm1 is started
    Then VM vm1 gets scheduled on host 127.0.0.5

  Scenario: Start a VM with two hosts, one of them is does not match required amount of cores
    Given a VM:
      | name | MinRam | MaxRam | CPUs | Architecture |
      | vm1  | 1G     | 2G     | 2    | x86_64       |
    And hosts:
      | address   | ram | Cores | Threads | Architecture |  |
      | 127.0.0.5 | 2G  | 2     | 4       | X86_64       |  |
      | 127.0.0.6 | 2G  | 1     | 2       | X86_64       |  |
    When VM vm1 is started
    Then VM vm1 gets scheduled on host 127.0.0.5

  Scenario: Start a VM with two hosts, one of them is does not match required amount of RAM
    Given a VM:
      | name | MinRam | MaxRam | CPUs | Architecture |
      | vm1  | 1G     | 2G     | 2    | x86_64       |
    And hosts:
      | address   | ram | Cores | Threads | Architecture |  |
      | 127.0.0.5 | 2G  | 2     | 4       | X86_64       |  |
      | 127.0.0.6 | 1G  | 2     | 4       | X86_64       |  |
    When VM vm1 is started
    Then VM vm1 gets scheduled on host 127.0.0.5

