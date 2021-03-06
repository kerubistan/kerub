Feature: Accounts

  Scenario: Accounts required feature enabled, user is not member
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
	And User enduser is not able to find vm vm-1 by name
	And User enduser is not able to list vm vm-1
	And User enduser is not able to update vm vm-1
	And User enduser is not able to remove vm vm-1
	And User enduser is not able to start vm vm-1
	And User enduser is not able to stop vm vm-1
	And User enduser is not able to create virtual disk outside of accounts
	And User enduser is not able to see virtual disk disk-1
	And User enduser is not able to list virtual disk disk-1
	And User enduser is not able to update virtual disk disk-1
	And User enduser is not able to find virtual disk disk-1 by name
	And User enduser is not able to remove virtual disk disk-1
	And User enduser is not able to upload virtual disk disk-1
	And User enduser is not able to create virtual network outside of accounts
	And User enduser is not able to see virtual network net-1
	And User enduser is not able to see virtual network net-1
	And User enduser is not able to see virtual network net-1
	And User enduser is not able to see virtual network net-1
	And User enduser is not able to find virtual network net-1 by name
	And User enduser is not able to subscribe vm vm-1
	And User enduser is not able to subscribe virtual disk disk-1
	And User enduser is not able to subscribe virtual network net-1
	And User enduser is not able to search vm vm-1
	And User enduser is not able to search virtual disk disk-1
	And User enduser is not able to search virtual network net-1

  Scenario: Accounts required feature enabled, user is member
	Given accounts:
	  | name             |
	  | EugeneCuckoo Inc |
	And Accounts are required
	And User enduser is member of EugeneCuckoo Inc
	And There are VMs defined:
	  | name | account          |
	  | vm-1 | EugeneCuckoo Inc |
	And There are disks defined:
	  | name   | account          |
	  | disk-1 | EugeneCuckoo Inc |
	And There are networks defined:
	  | name  | account          |
	  | net-1 | EugeneCuckoo Inc |
	Then User enduser is able to see the account EugeneCuckoo Inc
	And User enduser is not able to create vm outside of accounts
	And User enduser is able to see vm vm-1
	And User enduser is able to find vm vm-1 by name
	And User enduser is able to list vm vm-1
	And User enduser is able to update vm vm-1
	And User enduser is able to start vm vm-1
	And User enduser is able to stop vm vm-1
	And User enduser is able to search vm vm-1
#	And User enduser is able to subscribe vm vm-1
	And User enduser is able to remove vm vm-1
	And User enduser is not able to create virtual disk outside of accounts
	And User enduser is able to see virtual disk disk-1
	And User enduser is able to find virtual disk disk-1 by name
	And User enduser is able to list virtual disk disk-1
	And User enduser is able to update virtual disk disk-1
	#And User enduser is able to upload virtual disk disk-1
	And User enduser is able to search virtual disk disk-1
#	And User enduser is able to subscribe virtual disk disk-1
	And User enduser is able to remove virtual disk disk-1
	And User enduser is not able to create virtual network outside of accounts
	And User enduser is able to see virtual network net-1
	And User enduser is able to list virtual network net-1
	And User enduser is able to find virtual network net-1 by name
	And User enduser is able to update virtual network net-1
	And User enduser is able to search virtual network net-1
#	And User enduser is able to subscribe virtual network net-1
	And User enduser is able to remove virtual network net-1

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

  Scenario: Users in different accounts
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
	And user testuser-1 should see only ec-1 in vm list
	And user testuser-2 should see only jd-1 in vm list
	And user testuser-1 should see only ec-disk-1 in disk list
	And user testuser-2 should see only jd-disk-1 in disk list
	And user testuser-1 should see only ec-net-1 in network list
	And user testuser-2 should see only jd-net-1 in network list
	And User testuser-1 is able to search vm ec-1
	And User testuser-1 is able to search virtual disk ec-disk-1
	And User testuser-1 is able to search virtual network ec-net-1
	And User testuser-2 is not able to search vm ec-1
	And User testuser-2 is not able to search virtual disk ec-disk-1
	And User testuser-2 is not able to search virtual network ec-net-1
	And User testuser-2 is able to search vm jd-1
	And User testuser-2 is able to search virtual disk jd-disk-1
	And User testuser-2 is able to search virtual network jd-net-1
	And User testuser-1 is not able to search vm jd-1
	And User testuser-1 is not able to search virtual disk jd-disk-1
	And User testuser-1 is not able to search virtual network jd-net-1

