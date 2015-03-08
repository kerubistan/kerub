
Feature: Host Management Security

    Scenario Outline: Security
        Given A controller
        And <user> user in role User
        When user tries to <action> hosts
        Then <answer> should be received

    Examples: Illegal actions
        | action | user       | answer |
        | join   | User1      | 403    |
        | list   | User1      | 403    |
        | remove | User1      | 403    |
        | update | User1      | 403    |
        | join   | PowerUser1 | 403    |
        | list   | PowerUser1 | 403    |
        | remove | PowerUser1 | 403    |
        | update | PowerUser1 | 403    |

    Examples: Legal actions
        | action | user       | answer |
        | join   | Admin1     | 200    |
        | list   | Admin1     | 200    |
        | remove | Admin1     | 200    |
        | update | Admin1     | 200    |

