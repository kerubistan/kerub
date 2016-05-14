var NewVmWizard = function($scope, $uibModalInstance, $http, $log, $timeout, appsession, uuid4, size, expectations) {
	var id = uuid4.generate();
	$scope.vm = {
		"@type" : 'vm',
		id : id,
		name : 'vm-' + id,
		nrOfCpus : 1,
    	memory : {
    		min : size.toSize('512 MB'),
    		humanFriendlyMin : function(newMin) {
    			if(newMin) {
    				$scope.vm.memory.min = size.toSize(newMin);
    			}
    			return size.humanFriendlySize($scope.vm.memory.min);
    		},
    		max : size.toSize('1 GB'),
    		humanFriendlyMax : function(newMax) {
    			if(newMax) {
    				$scope.vm.memory.max = size.toSize(newMax);
    			}
    			return size.humanFriendlySize($scope.vm.memory.max);
    		}
    	},
    	expectations : [],
    	virtualStorageLinks : []
	};
	$scope.addVm = function() {
		appsession.put('s/r/vm', $scope.vm).success(function() {
        	$uibModalInstance.close();
		});
	};
    $scope.close = function() {
        $log.info('closed new vm dialog');
        $uibModalInstance.dismiss('cancel');
    };
    $scope.addStorageLink = function(link) {
    	$scope.vm.virtualStorageLinks.push(link);
    };

	$scope.storageExpectationsOpen = false;
	$scope.storageExpectationFormOpen = false;
	$scope.vmExpectations = filterValues(expectations, function(exp) { return exp.virtTypes.includes("vm") } );

}