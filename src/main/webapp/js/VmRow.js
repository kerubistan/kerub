kerubApp.controller('VmRow', function($location, $scope, $log, socket, appsession) {
    $scope.vm = {};
    $scope.vmdyn = {};
    $scope.workingprg = false;
    $scope.startVm = function() {
    	$scope.workingprg = true;
    	appsession.post('s/r/vm/' + $scope.vm.id + '/start').then(function() {
    		$scope.workingprg = false;
    	});
    };
    $scope.deleteVm = function() {
    	$scope.workingprg = true;
    	appsession.delete('s/r/vm/' + $scope.vm.id).then(function() {
    		$scope.workingprg = false;
    	});
    };
    $scope.spiceConnect = function() {
		window.open('s/r/vm-dyn/'+$scope.vm.id+'/connection/spice', '__new')
    };
    $scope.templateFromVm = function() {
    	$scope.workingprg = true;
    	appsession.put('s/r/template/helpers/from-vm/' + $scope.vm.id).then(function() {
    		$scope.workingprg = false;
    	});
    };
    $scope.stopVm = function() {
    	$scope.workingprg = true;
    	appsession.post('s/r/vm/' + $scope.vm.id + '/stop').then(function() {
    		$scope.workingprg = false;
    	});
    };
    $scope.cpuSum = function() {
    	var sum = 0;
		angular.forEach($scope.vmdyn.cpuUsage, function(cpu) {
			sum += cpu.user + cpu.system;
		});
    };
    $scope.init = function(vm) {
    	$scope.vm = vm;
    	socket.subscribe('/vm-dyn/'+$scope.vm.id, function(event) {
    		$scope.$apply(function() {
				if(event['@type'] == 'entity-update' && event.obj['@type'] == 'vm-dyn') {
					$scope.vmdyn = event.obj;
				}
    		});
    	}, 'VmRow');
    };
    $scope.destroy = function() {
		socket.unsubscribe('vm-dyn/'+$scope.vm.id);
    }
});