Feature: Authentication and authorization on the rest api

  Scenario Outline: Unauthenticated attempt to perform an action
    Given anonymous user
    When user tries to <action>
    Then request must be rejected
    And the response code must be 401
    And the error code must be AUTH1
    And no session should be created

    Examples:
      | action              |
      | retrieve host list  |
      | retrieve vm list    |
      | join new host       |
      | create new vm       |
      | get host public key |

  Scenario: Authentication errors on login attempt
    Given user haxor with password seecret
    When user tries to log in
    Then request must be rejected
    And the response code must be 401
    And the error code must be AUTH2
    And no session should be created

  Scenario Outline: some services should be readable by anyone
    Given anonymous user
    When user tries to get <what>
    Then request must pass
    And no session should be created

    Examples:
      | what    |
      | motd    |
      | version |

  Scenario Outline: Authentication pass with known users
    Given user <user> with password <password>
    When user tries to log in
    Then request must pass
    And session should be created

    Examples:
      | user      | password   |
      | admin     | password   |
      | poweruser | password   |
      | enduser   | password   |