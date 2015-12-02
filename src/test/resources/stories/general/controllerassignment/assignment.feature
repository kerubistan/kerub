Feature: virtual and physical resources are assigned to controllers

  Scenario: virtual machine creation
    Given controllers:
      | name    |
      | CTRL-1  |
      | CTRL-2  |
    And hosts:
      | address             | cpus | architecture | memory |
      | host-1.example.com  | 2    | X86_64       | 16 GB  |
      | host-2.example.com  | 2    | X86_64       | 16 GB  |
      | host-3.example.com  | 2    | X86_64       | 16 GB  |
      | host-4.example.com  | 2    | X86_64       | 16 GB  |
    And host assignments
      | address             | controller |
      | host-1.example.com  | CTRL-1     |
      | host-2.example.com  | CTRL-1     |
      | host-3.example.com  | CTRL-1     |
      | host-4.example.com  | CTRL-2     |
    When virtual machine vm-1 is created
    Then virtual machine vm-1 should be assigned to controller CTRL-1 because more