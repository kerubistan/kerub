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
    	],
    	expectations : [],
    	virtualStorageLinks : []
	};
	$scope.addVm = function() {
		appsession.put('s/r/vm', $scope.vm).success(function() {
        	$uibModalInstance.close();
		});
	};
	$scope.autoName = function() {
		appsession.get('s/r/vm/autoname').then(function(result) {
			$scope.vm.name = result;
			$scope.checkVMName();
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

	$scope.anyDevicesOfType = function(type) {
		var found = false;
		angular.forEach($scope.vm.devices, function(it) {
			if(it['@type'] == type) {
				found = true;
			}
		});
		$log.info('devices of type ', type, found);
	};

	$scope.checkVMName = function() {
		var name = $scope.vm.name;
		if(name.length > 0) {
			$scope.validating = true;
			appsession.get('s/r/vm/byname/' + name).success(function(results) {
				if(name == $scope.vm.name) {
					$scope.valid = results.length == 0;
					$scope.validating = false;
				}
			});
		} else {
			$scope.valid = false;
			$scope.validating = false;
		}
	};

	$scope.openWatchdogDeviceForm = function() {
		if($scope.anyDevicesOfType('watchdog')) {
			return;
		}
		$scope.devicesMode = 'add-watchdog';
		$scope.device = {
			'@type' : 'watchdog',
			'type' : 'i6300esb',
			'action' : 'reset'
		};
	};

	$scope.openNICDeviceForm = function() {
		$log.info('openNICDeviceForm');
		$scope.devicesMode = 'add-nic';
		$scope.device = {
			'@type' : 'nic',
			'adapterType' : 'e1000',
			'networkId' : null
		};
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


	$scope.autoName();
	$scope.vmExpectationsOpen = false;
	$scope.vmExpectationFormOpen = false;
	$scope.vmExpectations = filterValues(expectations, function(exp) { return exp.virtTypes.includes("vm") } );

}