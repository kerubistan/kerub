
Feature: Host Management

    Scenario: Host public key signature
        Given a host
        When host public key is retrieved
        Then the host public must match the retrieved public key

    Scenario: Host Discovery
        Given a host
        When the host is joined
        Then host will be assigned to a controller
        And host capabilities will be filled
        And the host will connected

    Scenario: