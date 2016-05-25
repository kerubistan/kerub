Feature: Planner and Executor integration

  Scenario: Planner does not send another plan until execution finishes
	Given a dummy executor
	And the planner
	And a host
	And a VM
	When the VM gets started
	And planner receives events
	Then executor should receive exactly 1 plan to execute

  Scenario: Planner does not send another plan until execution finishes - same with disk
	Given a dummy executor
	And the planner
	And a host
	And a disk
	When the disk needs allocation
	And planner receives events
	Then executor should receive exactly 1 plan to execute

