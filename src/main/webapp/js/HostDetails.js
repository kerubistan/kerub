var HostDetails = function($scope, $log, $modalInstance, appsession, hostId, size, socket) {
    $scope.host = null;

    $scope.tab = "overview";
    $scope.nrOfCpus = null;

    $scope.dyn = {};

	$scope.humanFriendlySize = size.humanFriendlySize;

	$log.info('host id ',hostId);

    socket.subscribe('/host-dyn/'+hostId, function(msg) {
        $scope.dyn = msg.obj;
    	$log.info("host status: " + $scope.dyn.status)
    }, 'HostDetails');

	$scope.iconByStatus = function() {
        switch($scope.dyn.status) {
        	case 'Up':
        		return 'fa fa-sun-o';
        	case 'Down':
        		return 'fa fa-down-o';
        	default:
        		return 'fa fa-question'
        }
	}

    appsession
        .get('s/r/host/'+hostId)
        .success(function(host) {
            $log.debug('host details received');
            $scope.host = host;
            $scope.nrOfCpus = host.capabilities && host.capabilities.cpus ? host.capabilities.cpus.length : 1;
        });

    $scope.selectView = function(view) {
        $log.info('select view ', view);
        $scope.tab = view;
    };

    $scope.isActive = function(view) {
        if($scope.tab === view) {
            return 'active'
        } else {
            return '';
        }
    };

    $scope.isVisible = function(view) {
        return $scope.tab === view
    };

}
