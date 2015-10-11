var NewVmWizard = function($scope, $modalInstance, $http, $log, $timeout, $appsession, uuid4) {
	$scope.vm = {
		"@type" : 'vm',
		id : uuid4.generate(),
		nrOfCpus : 1,
    	memory : {
    		min : 512,
    		max : 1024
    	},
    	expectations : [],
    	storagedevices : []
	};
	$scope.addVm = function() {
		$appsession.put('s/r/vm', $scope.vm).success(function() {
        	$modalInstance.close();
		});
	};
    $scope.close = function() {
        $log.info('closed new vm dialog');
        $modalInstance.dismiss('cancel');
    };
}