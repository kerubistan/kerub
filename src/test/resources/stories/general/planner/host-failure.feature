Feature: planner reactions on host failures

  Scenario: If a host fails, virtual machines must be started on another host
    Given hosts:
      | address           | ram  | Cores | Threads | Architecture |  |
      | host1.example.com | 6 GB | 2     | 4       | x86_64       |  |
      | host2.example.com | 6 GB | 2     | 4       | x86_64       |  |
    And VMs:
      | name | MinRam | MaxRam | CPUs | Architecture |
      | vm1  | 4 GB   | 4 GB   | 2    | x86_64       |
    And host host1.example.com is Up
    And host host2.example.com is Up
    And vm1 is running on host1.example.com
    And vm1 has safe power-management
    When host1.example.com fails
    And VM vm2 gets scheduled on host host2.example.com

  Scenario: If a host with no power manager fails, it must be fenced and virtual machines started on another host
