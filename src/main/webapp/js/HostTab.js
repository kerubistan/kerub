
kerubApp.controller('HostTab', function($scope, $uibModal, $log, socket, appsession, size) {

	$scope.humanFriendlySize = size.humanFriendlySize;

	$scope.hosts = {};
    $scope.itemsPerPage = 10;
    $scope.currentPage = 1;

    $scope.newHostForm = function() {
        var modalInstance = $uibModal.open({
            templateUrl : 'NewHostWizard.html',
            controller : NewHostWizard
        });
        $log.debug(modalInstance);
    };

	$scope.refresh = function() {
		appsession.get('s/r/host?start='
			+ ($scope.itemsPerPage * ($scope.currentPage -1))
			+ '&limit=' + $scope.itemsPerPage
			+ '&sort=address').success(function(hostsResult) {
			$scope.hosts = hostsResult;
		});
	}
	$scope.refresh();

    socket.subscribe('/host', function(msg) {
    	$log.info(msg);
    	switch(msg['@type']) {
    		case 'entity-remove':
    			$log.info('remove ' + msg.obj.id);
    			$scope.refresh();
    			break;
    		case 'entity-update':
    			var updatedHostId = msg.obj.id;
    			for(var idx = 0; idx < $scope.hosts.result.size; idx ++) {
    				if($scope.hosts.result[idx].id === id) {
    					$scope.hosts.result[idx] = msg.obj;
    				}
    			}
    			break;
    		case 'entity-add':
    			if($scope.hosts.result.length < $scope.itemsPerPage) {
					$scope.hosts.result.push(msg.obj);
    			}
    			break;
    	}
    }, 'HostTab');


    $scope.showHostDetails = function(hostId) {
        var modalInstance = $uibModal.open({
            templateUrl : 'HostDetails.html',
            controller : HostDetails,
            resolve : {
                hostId : function() {
                    return hostId;
                }
            }
            });
    }


});
