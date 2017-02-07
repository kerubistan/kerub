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
    	devices  : [
    		{
    			'@type':'watchdog',
    			'type' : 'i6300esb',
    			'action' : 'reset'
    		}
    	],
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

	$scope.devicesMode = 'overview';

	$scope.openWatchdogDeviceForm = function() {
		$scope.devicesMode = 'add-watchdog';
	};

	$scope.openNICDeviceForm = function() {
		$scope.devicesMode = 'add-nic';
	};

	$scope.openExpectationForm = function(expType) {
		$scope.vmExpectationsOpen = false;
		$scope.vmExpectationFormOpen = true;
		$scope.newExpectation = expType;
	};

	$scope.closeExpectationForm = function() {
		$scope.vmExpectationsOpen = true;
		$scope.vmExpectationFormOpen = false;
		$scope.newExpectation = null;
	};

	$scope.vmExpectationsOpen = false;
	$scope.vmExpectationFormOpen = false;
	$scope.vmExpectations = filterValues(expectations, function(exp) { return exp.virtTypes.includes("vm") } );

}