Feature: error codes of the rest api

  Scenario: host join error
    Given a host address notexisting.example.com
    And user admin with password password
    When the client tries to join the server
    Then the response code must be 406
    And the content type must be application/json
    And the error code must be HOST2

  Scenario: host join error
    Given a host address notexisting.example.com
    And user admin with password password
    When the client tries to join the server with password omgpasswd123
    Then the response code must be 406
    And the content type must be application/json
    And the error code must be HOST2

  Scenario: host pubkey aquire
    Given a host address notexisting.example.com
    And user admin with password password
    When the client tries to retrieve public key
    Then the response code must be 406
    And the content type must be application/json
    And the error code must be HOST2
