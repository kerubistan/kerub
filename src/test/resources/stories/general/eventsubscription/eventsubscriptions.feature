Feature: event subscription

  Scenario: Basic behavior
    Given virtual machine vm-1
    And a session session-1
    And a session session-2
    And session-1 subscribes to events on vm-1
    And session-1 does not subscribe to events on vm-1
    When vm-1 is updated
    Then session session-1 must get a notification
    And session session-2 must not get a notification
