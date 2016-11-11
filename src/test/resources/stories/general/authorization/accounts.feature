Feature: Accounts

  Scenario: Accounts required feature enabled
	Given accounts:
	  | name             |
	  | EugeneCuckoo Inc |
	And Accounts are required
	And User enduser is not member of any account
	And There are VMs defined:
	  | name | account          |
	  | vm-1 | EugeneCuckoo Inc |
	And There are disks defined:
	  | name   | account          |
	  | disk-1 | EugeneCuckoo Inc |
	And There are networks defined:
	  | name  | account          |
	  | net-1 | EugeneCuckoo Inc |
	Then User enduser is not able to see the account EugeneCuckoo Inc
	And User enduser is not able to create vm outside of accounts
	And User enduser is not able to see vm vm-1
	And User enduser is not able to list vm vm-1
	And User enduser is not able to update vm vm-1
	And User enduser is not able to remove vm vm-1
	And User enduser is not able to start vm vm-1
	And User enduser is not able to stop vm vm-1
	And User enduser is not able to create virtual disk outside of accounts
	And User enduser is not able to see virtual disk disk-1
	And User enduser is not able to list virtual disk disk-1
	And User enduser is not able to update virtual disk disk-1
	And User enduser is not able to remove virtual disk disk-1
	And User enduser is not able to upload virtual disk disk-1
	And User enduser is not able to create virtual network outside of accounts
	And User enduser is not able to see virtual network net-1
	And User enduser is not able to see virtual network net-1
	And User enduser is not able to see virtual network net-1
	And User enduser is not able to see virtual network net-1

  Scenario: Accounts required feature enabled, user can create vms in the account he is member of
	Given accounts:
	  | name             |
	  | EugeneCuckoo Inc |
	And Accounts are required
	And User enduser is member of EugeneCuckoo Inc
	Then User enduser is able to see the account EugeneCuckoo Inc
	And user enduser is able to create vm under account EugeneCuckoo Inc
	And user enduser is able to create virtual network under account EugeneCuckoo Inc
	And user enduser is able to create virtual disk under account EugeneCuckoo Inc

  Scenario: Accounts
	Given accounts:
	  | name              |
	  | EugeneCuckoo Inc  |
	  | JohnDoe & Co Corp |
	And Accounts are required
	And User testuser-1 is member of EugeneCuckoo Inc
	And User testuser-2 is member of JohnDoe & Co Corp
	And There are VMs defined:
	  | name | account           |
	  | ec-1 | EugeneCuckoo Inc  |
	  | jd-1 | JohnDoe & Co Corp |
	And There are disks defined:
	  | name      | account           |
	  | ec-disk-1 | EugeneCuckoo Inc  |
	  | jd-disk-1 | JohnDoe & Co Corp |
	And There are networks defined:
	  | name     | account           |
	  | ec-net-1 | EugeneCuckoo Inc  |
	  | jd-net-1 | JohnDoe & Co Corp |
	Then testuser-1 is not able to create vm with disk ec-disk-1 in account JohnDoe & Co Corp
	And testuser-2 is not able to create vm with disk jd-disk-1 in account EugeneCuckoo Inc

