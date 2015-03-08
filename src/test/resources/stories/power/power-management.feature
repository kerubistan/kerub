
Feature: Host power management

    Scenario: Wake-on-Lan + ssh "power management" power off
    Given a joined host:
        |  |
    When the host is powered down
    Then the following 'poweroff' command is executed

    Scenario: Wake-on-Lan + ssh "power management" power off
    Given a joined host:
        | address   | OS    |
        | 127.0.0.2 | linux |
    When the host is powered down
    Then the following 'poweroff' command is executed
