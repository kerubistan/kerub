var HostDetails = function($scope, $log, appsession, hostId, size, socket) {
    $scope.host = null;

    $scope.tab = "overview";
    $scope.nrOfCpus = null;

    $scope.dyn = {};

	$scope.humanFriendlySize = size.humanFriendlySize;

	$log.info('host id ',hostId);

    socket.subscribe('/host-dyn/'+hostId, function(msg) {
    	$scope.$apply(function() {
			$log.info('host dynamic update', msg.obj)
			$scope.dyn = msg.obj;
    	});
    }, 'HostDetails');

    appsession
        .get('s/r/host/'+hostId)
        .success(function(host) {
            $scope.host = host;
            $scope.nrOfCpus = host.capabilities && host.capabilities.cpus ? host.capabilities.cpus.length : 1;
        });

	$scope.hostStatusIcon = function() {
		if($scope.dyn.status === 'Up') {
			return 'fa fa-sun-o';
		} else {
		    return 'fa fa-moon-o';
		}
	};

	$scope.hostMemUsedPercent = function() {
		$log.info('hostMemUsedPercent');
		var totalMemory = $scope.host.capabilities.totalMemory ? $scope.host.capabilities.totalMemory : 1;
		var usedMemory = $scope.dyn.memUsed ? $scope.dyn.memUsed : 0;
		$log.info(totalMemory, usedMemory, (usedMemory * 100) / totalMemory);
        return (usedMemory * 100) / totalMemory;
	}

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
