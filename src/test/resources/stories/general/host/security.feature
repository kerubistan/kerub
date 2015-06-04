
Feature: Host Management Security

    Scenario Outline: Security
        Given A controller
        And <user> user
        When user tries to <action> hosts
        Then <answer> should be received


    Examples: Illegal actions
        | action | user       | answer |
        | join   | enduser    | 403    |
        | list   | enduser    | 403    |
        | remove | enduser    | 403    |
        | update | enduser    | 403    |
        | join   | poweruser  | 403    |
        | list   | poweruser  | 403    |
        | remove | poweruser  | 403    |
        | update | poweruser  | 403    |

    #well, some were removed and checked in actual integration tests
    Examples: Legal actions
        | action | user       | answer |
        | list   | admin      | 200    |

