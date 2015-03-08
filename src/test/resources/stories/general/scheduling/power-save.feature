
Feature: Power-Save

    Scenario: A single unused host
        Given registered host:
            | ip-address | dedicated |
            | 127.0.0.1  | true      |
        And no virtual machines
        When optimization is triggered
        Then host 127.0.0.1 should be go to power-save

    Scenario: Two hosts, only one VM
        Given registered host:
            | ip-address | dedicated |
            | 127.0.0.2  | true      |
            | 127.0.0.3  | true      |
        And VMs:
            | name |
            | vm1  |
        And status:
            | name | runsOn    |
            | vm1  | 127.0.0.2 |
        When optimization is triggered
        Then host 127.0.0.1 should be go to power-save
