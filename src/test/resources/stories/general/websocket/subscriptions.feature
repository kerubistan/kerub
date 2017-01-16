Feature: Subscription to message feeds

  Scenario Outline: User is not subscribed, should not receive any notifications
	Given Controller configuration 'accounts required' is disabled
	And VM:
	  | property | value             |
	  | name     | websocket-test-vm |
	  | nrOfCpus | 1                 |
	  | memory   | 128MB             |
	And Virtual Network:
	  | property | value              |
	  | name     | websocket-test-net |
	And Virtual Disk:
	  | property | value               |
	  | name     | websocket-test-disk |
	And <listener> is connected to websocket
 # user not subscribed to message feed <feed>
	When the user <action-user> <action> <entity-type> <entity-name>
	Then listener user must not receive socket notification

	Examples:
	  | listener   | action-user | action  | entity-type     | entity-name         |
	  #own actions
	  | enduser    | enduser     | creates | vm              |                     |
	  | enduser    | enduser     | creates | virtual network | websocket-test-net  |
	  | enduser    | enduser     | creates | virtual disk    | websocket-test-disk |
	  #other's actions
	  | testuser-2 | testuser-1  | creates | vm              |                     |
	  | testuser-2 | testuser-1  | creates | virtual network | websocket-test-net  |
	  | testuser-2 | testuser-1  | creates | virtual disk    | websocket-test-disk |
# TODO this would be great to have but *adding* hosts is not possible without connecting them, also there is no remove operation yet
#	  | admin    | admin       | creates | host            |
#	  | enduser  | admin       | creates | host            |

  Scenario Outline: User subscribes to events on <entity-type> and receives messages
	Given Controller configuration 'accounts required' is disabled
	And VM:
	  | property | value             |
	  | name     | websocket-test-vm |
	  | nrOfCpus | 1                 |
	  | memory   | 128MB             |
	And Virtual Network:
	  | property | value              |
	  | name     | websocket-test-net |
	And Virtual Disk:
	  | property | value               |
	  | name     | websocket-test-disk |
	And <listener> is connected to websocket
	And user subscribed to message feed <feed>
	When the user <action-user> <action> <entity-type> <entyty-name>
	Then listener user <must-get-message> receive socket notification with type <nofification-type>

	Examples:
	  | listener | action-user | action  | entity-type     | entyty-name         | feed             | must-get-message | nofification-type |
	  | enduser  | enduser     | creates | vm              |                     | /vm              | must             | create            |
	  | enduser  | enduser     | updates | vm              | websocket-test-vm   | /vm              | must             | update            |
	  | enduser  | enduser     | deletes | vm              | websocket-test-vm   | /vm              | must             | delete            |
	  | enduser  | enduser     | creates | virtual network |                     | /vnet            | must             | create            |
	  | enduser  | enduser     | updates | virtual network | websocket-test-net  | /vnet            | must             | update            |
	  | enduser  | enduser     | deletes | virtual network | websocket-test-net  | /vnet            | must             | delete            |
	  | enduser  | enduser     | creates | virtual disk    |                     | /virtual-storage | must             | create            |
	  | enduser  | enduser     | updates | virtual disk    | websocket-test-disk | /virtual-storage | must             | update            |
	  | enduser  | enduser     | deletes | virtual disk    | websocket-test-disk | /virtual-storage | must             | delete            |
# TODO this would be great to have but *adding* hosts is not possible without connecting them, also there is no remove operation yet
#	  | admin    | admin       | creates | host            |                     | /host     | must             | create            |
#	  | enduser  | admin       | creates | host            |                     | /host     | must not         | create            |
